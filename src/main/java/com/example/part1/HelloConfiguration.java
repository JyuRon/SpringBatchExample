package com.example.part1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class HelloConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public HelloConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory){
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job helloJob(){
        return jobBuilderFactory.get("helloJob")        // Job의 이름
                .incrementer(new RunIdIncrementer())    // 실행 단위       // 항상 Job이 실행 할때마다 파라미터 아이디를 자동으로 생성
                .start(this.helloStep())                // Job init
                .build();
    }

    // step : job의 실행 단위, Job은 여러개의 step을 가질 수 있다.
    @Bean
    public Step helloStep(){
        return stepBuilderFactory.get("helloStep")
                .tasklet((contribution, chunkContext) -> {      // step의 실행 단위 : task기반과 junk(?)기반이 존재
                    log.info("hello spring batch");
                    return RepeatStatus.FINISHED;
                }).build();

    }

}
