package com.example;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableBatchProcessing
public class SrpingBatchExampleApplication {

	public static void main(String[] args) {
		System.exit(SpringApplication.exit(SpringApplication.run(SrpingBatchExampleApplication.class, args)));
	}

	@Bean
	@Primary	// 같은 빈이 존재 할 경우 우선순위로 사용
	TaskExecutor taskExecutor(){
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(10);	// 기본 thread size
		taskExecutor.setMaxPoolSize(20); 	// 최대 thread size
		taskExecutor.setThreadNamePrefix("batch-thread-");
		return taskExecutor;
	}

}
