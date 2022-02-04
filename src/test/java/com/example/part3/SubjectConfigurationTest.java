package com.example.part3;

import com.example.TestConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedList;


@SpringBatchTest    // JobScope등의 문제를 해결
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SubjectConfiguration.class, TestConfiguration.class})
public class SubjectConfigurationTest {

    LinkedList A;


    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private PersonRepository personRepository;

    @After
    public void tearDown(){
        personRepository.deleteAll();
    }

    @Test
    public void test_step(){

        // 파라미터 전달도 가능하다다
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("subjectStep");

        Assertions.assertThat(jobExecution.getStepExecutions().stream()
                        .mapToInt(StepExecution::getWriteCount)
                        .sum())
                .isEqualTo(personRepository.count())
                .isEqualTo(3);
    }


    @Test
    public void test_allow_duplicate() throws Exception {
        // given
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("allow_duplicate", "false")
                .toJobParameters();

        // when
        // SubjectConfiguration.class에서 설장한 Job이 실행되는 것
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);


        // then
        // JobExcution은 N개의 Step을 가질 수 있다.
        Assertions.assertThat(jobExecution.getStepExecutions().stream()
                        .mapToInt(StepExecution::getWriteCount)
                        .sum())
                .isEqualTo(personRepository.count())
                .isEqualTo(3);

    }

    @Test
    public void test_not_allow_duplicate() throws Exception {
        // given
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("allow_duplicate", "true")
                .toJobParameters();

        // when
        // SubjectConfiguration.class에서 설장한 Job이 실행되는 것
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);


        // then
        // JobExcution은 N개의 Step을 가질 수 있다.
        Assertions.assertThat(jobExecution.getStepExecutions().stream()
                        .mapToInt(StepExecution::getWriteCount)
                        .sum())
                .isEqualTo(personRepository.count())
                .isEqualTo(100);

    }
}
