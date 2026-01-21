package com.portfolio.aips.project.utils.virtual_thread_utils;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;



@Slf4j
public class VirtualThreadBoundedExecutor implements BoundedExecutor {

    private final ExecutorService executor;
    private final Semaphore semaphore;
    private final long timeout;
    private final TimeUnit unit;

    private VirtualThreadBoundedExecutor(
            ExecutorService executor,
            Semaphore semaphore,
            long timeout,
            TimeUnit unit
    ) {
        this.executor = executor;
        this.semaphore = semaphore;
        this.timeout = timeout;
        this.unit = unit;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void execute(Runnable task) {
        executor.submit(() -> runWithSemaphore(task));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return executor.submit(() -> callWithSemaphore(task));
    }

    public  <T> List<T> join(List<Future<List<T>>> futures) {
        try (ExecutorService vt = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<List<T>> joined = vt.submit(() ->
                    futures.stream()
                            .map(f -> {
                                try {
                                    return f.get();
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .flatMap(List::stream)
                            .toList()
            );
            return joined.get();
        } catch (Exception e) {
            throw new RuntimeException("VT join failed", e);
        }
    }

    @Override
    public <T> void executeBatched(
            BlockingQueue<T> queue,
            int batchSize,
            Consumer<List<T>> task
    ) {
        while (!queue.isEmpty()) {
            List<T> batch = new ArrayList<>(batchSize);
            queue.drainTo(batch, batchSize);
            if (batch.isEmpty()) return;


            executor.submit(
                    () ->
                    {
                        runWithSemaphore(task, batch);
                        log.info("{} task completed", batch.size());
                    }
                    );


        }
    }

    private void runWithSemaphore(Runnable task) {
        if (!acquire()) throw new RejectedExecutionException("Semaphore timeout");
        try {
            task.run();
        } finally {
            semaphore.release();
        }
    }

    private <T>void runWithSemaphore(Consumer<List<T>> task, List<T> batch) {
        if (!acquire()) throw new RejectedExecutionException("Semaphore timeout");
        try {
            task.accept(batch);
        } finally {
            semaphore.release();
        }
    }


    private <T> T callWithSemaphore(Callable<T> task) throws Exception {
        if (!acquire()) throw new RejectedExecutionException("Semaphore timeout");
        try {
            return task.call();
        } finally {
            semaphore.release();
        }
    }

    private boolean acquire() {
        try {
            if (!semaphore.tryAcquire(timeout, unit)) {
                log.warn("세마포어 타임 아웃");
                return false;
            }
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    // -------- Builder --------
    public static class Builder {
        private ExecutorService executor;
        private Semaphore semaphore;
        private long timeout = 5;
        private TimeUnit unit = TimeUnit.SECONDS;

        public Builder executor(ExecutorService executor) {
            this.executor = executor;
            return this;
        }

        public Builder semaphore(Semaphore semaphore) {
            this.semaphore = semaphore;
            return this;
        }

        public Builder timeout(long timeout, TimeUnit unit) {
            this.timeout = timeout;
            this.unit = unit;
            return this;
        }

        public VirtualThreadBoundedExecutor build() {
            return new VirtualThreadBoundedExecutor(
                    executor, semaphore, timeout, unit
            );
        }
    }
}

