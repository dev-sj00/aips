package com.portfolio.aips.project.config.virtual_thread;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Configuration
public class VirtualThreadConfig {

    @Bean(destroyMethod = "shutdown")
    public ExecutorService vtExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean(name = "dbSemaphore")
    public Semaphore dbSemaphore() {
        return new Semaphore(15);
    }

    @Bean(name = "esSemaphore")
    public Semaphore esSemaphore() {
        return new Semaphore(8);
    }
}
