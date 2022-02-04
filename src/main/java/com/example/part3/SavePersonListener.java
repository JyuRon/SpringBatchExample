package com.example.part3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.batch.core.annotation.BeforeStep;

@Slf4j
public class SavePersonListener {

    public static class SavePersonStepExecutionListener{
        @BeforeStep
        public void beforeStep(StepExecution stepExecution){
            log.info("before step");
        }

        @AfterStep
        public ExitStatus afterStep(StepExecution stepExecution){
            log.info("afterStep : {}",stepExecution.getWriteCount());
            if(stepExecution.getWriteCount() == 0){
                return ExitStatus.FAILED;
            }
            return stepExecution.getExitStatus();
        }
    }

    public static class SavePersonJobExecutionListener implements JobExecutionListener{

        @Override
        public void beforeJob(JobExecution jobExecution) {
            log.info("beforeJob");
        }

        @Override
        public void afterJob(JobExecution jobExecution) {
            int sum = jobExecution.getStepExecutions().stream().mapToInt(StepExecution::getWriteCount).sum();

            log.info("afterJob : {}",sum);
        }
    }


    public static class SavePersonAnnotationJobExecution{

        @BeforeJob
        public void beforeJob(JobExecution jobExecution) {
            log.info("annotation beforjob");
        }

        @AfterJob
        public void afterJob(JobExecution jobExecution) {
            int sum = jobExecution.getStepExecutions().stream().mapToInt(StepExecution::getWriteCount).sum();

            log.info("annotation afterJob : {}",sum);
        }

    }

}
