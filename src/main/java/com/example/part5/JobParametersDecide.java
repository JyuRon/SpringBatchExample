package com.example.part5;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.util.StringUtils;

@Slf4j
public class JobParametersDecide implements JobExecutionDecider {

    public static final FlowExecutionStatus CONTINUE = new FlowExecutionStatus("CONTINUE");

    // JobParameters의 키
    private String key;

    public JobParametersDecide(String key) {
        this.key = key;
    }


    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        String value = jobExecution.getJobParameters().getString(key);

        log.info("decide key check : {}",value);

        // 파라미터 값이 없으면 완료처리
        if(!StringUtils.hasText(value)){
            return FlowExecutionStatus.COMPLETED;
        }

        // 파라미터 값이 있으면 실행
        return CONTINUE;
    }
}
