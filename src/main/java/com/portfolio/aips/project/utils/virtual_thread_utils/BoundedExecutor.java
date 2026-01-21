package com.portfolio.aips.project.utils.virtual_thread_utils;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public interface BoundedExecutor {
    void execute(Runnable task);

    <T> Future<T> submit(Callable<T> task);

     <T> List<T> join(List<Future<List<T>>> futures);


    <T> void executeBatched(
            BlockingQueue<T> queue,
            int batchSize,
            Consumer<List<T>> task
    );

}
