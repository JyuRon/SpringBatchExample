package com.example.part3;


import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class ChunkProcessingConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public ChunkProcessingConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job chunkProcessingJob(){
        return jobBuilderFactory.get("chunkProcessingJob")
                .incrementer(new RunIdIncrementer())
                .start(this.taskBaseStep())
                .next(this.chunkBaseStep(null)) // 인자가 null이여도 실행되는 이유 : @JobScope
                .build();
    }

    @Bean
    @JobScope
    public Step chunkBaseStep(@Value("#{jobParameters[chunkSize]}") String chunkSize){
        return stepBuilderFactory.get("chunkBaseStep")
                //<Input, Output>
                .<String,String>chunk(StringUtils.hasText(chunkSize)? Integer.parseInt(chunkSize) : 10)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Step taskBaseStep() {
        return stepBuilderFactory.get("taskBaseStep")
                .tasklet(this.tasklet2(null)) // 인자가 null이여도 실행되는 이유 : @StepScope
                .build();
    }

    // <Output>
    private ItemWriter<String> itemWriter() {
        return items -> log.info("chunk item size : {}", items.size());
//        return items -> items.forEach(log::info);
    }


    //<Input, Output>
    private ItemProcessor<String, String> itemProcessor() {
        // return null 일 경우 itemWiter로 넘어갈 수 없다

        return item -> item + ", Spring Batch";
    }

    // <Input>
    private ItemReader<String> itemReader() {
        return new ListItemReader<>(getItems());
    }



    // Tasklet으로 chunk와 유사하게 만들기
    // 파라미터 학습
    private Tasklet tasklet() {
        List<String> items = getItems();
        return (contribution, chunkContext) -> {
            StepExecution stepExecution = contribution.getStepExecution();
            JobParameters jobParameters = stepExecution.getJobParameters();

            // 환경 변수에 -chunkSize=20
            String value = jobParameters.getString("chunkSize","10");
            int chunkSize = StringUtils.hasText(value) ? Integer.parseInt(value) : 10;

            int fromIndex = stepExecution.getReadCount();   // start
            int toIndex = fromIndex + chunkSize;            // end

            if(fromIndex >= items.size()){
                return RepeatStatus.FINISHED;
            }

            List<String> subList = items.subList(fromIndex, toIndex);

            log.info("task item size : {}", subList.size());

            stepExecution.setReadCount(toIndex);

            // CONTINUABLE : 반복해서 실행한다
            return RepeatStatus.CONTINUABLE;
        };
    }

    // @Scope 학습
    // 환경 변수에 -chunkSize=20
    @Bean
    @StepScope
    public Tasklet tasklet2(@Value("#{jobParameters[chunkSize]}")String value) {
        List<String> items = getItems();
        return (contribution, chunkContext) -> {
            StepExecution stepExecution = contribution.getStepExecution();

            int chunkSize = StringUtils.hasText(value) ? Integer.parseInt(value) : 10;

            int fromIndex = stepExecution.getReadCount();   // start
            int toIndex = fromIndex + chunkSize;            // end

            if(fromIndex >= items.size()){
                return RepeatStatus.FINISHED;
            }

            List<String> subList = items.subList(fromIndex, toIndex);

            log.info("task item size : {}", subList.size());

            stepExecution.setReadCount(toIndex);

            // CONTINUABLE : 반복해서 실행한다
            return RepeatStatus.CONTINUABLE;
        };
    }


    private List<String> getItems() {
        List<String> items = new ArrayList<>();

        for(int i=0;i<100;i++){
            items.add(i+" Hello");
        }

        return items;
    }
}
