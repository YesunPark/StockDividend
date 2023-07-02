package com.zerobase.StockDividend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler threadPool = new ThreadPoolTaskScheduler();
        int n = Runtime.getRuntime().availableProcessors();
        threadPool.setPoolSize(n + 1);
        threadPool.initialize();

        taskRegistrar.setTaskScheduler(threadPool);
        // 우리가 생성한 스레드 풀을 사용하게 됨
    }
}
