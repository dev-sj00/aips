package com.portfolio.aips.project.config.virtual_thread;


import com.portfolio.aips.project.utils.virtual_thread_utils.BoundedExecutor;
import com.portfolio.aips.project.utils.virtual_thread_utils.VirtualThreadBoundedExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Configuration
public class VirtualThreadConfig {

    @Bean(destroyMethod = "shutdown")
    public ExecutorService vtExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean("dbBoundedExecutor")
    public BoundedExecutor dbBoundedExecutor() {
        return VirtualThreadBoundedExecutor
                .builder()
                .executor(vtExecutor())
                .semaphore(new Semaphore(15))
                .timeout(5, TimeUnit.SECONDS)
                .build();
    }

    @Bean("esBoundedExecutor")
    public BoundedExecutor esBoundedExecutor() {
        return VirtualThreadBoundedExecutor
                .builder()
                .executor(vtExecutor())
                .semaphore(new Semaphore(8))
                .timeout(5, TimeUnit.SECONDS)
                .build();
    }


}
