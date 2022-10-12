package no.nav.dolly.util;

import org.slf4j.MDC;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MdcWarraperExecutorService implements ExecutorService {
    private final ExecutorService wrapper;

    public MdcWarraperExecutorService(ExecutorService wrapper) {
        this.wrapper = wrapper;
    }
    @Override
    public void shutdown() {
        wrapper.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return wrapper.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return wrapper.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return wrapper.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return wrapper.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return wrapper.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return wrapper.submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return wrapper.submit(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return wrapper.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return wrapper.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return wrapper.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return wrapper.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        var contextMap = MDC.getCopyOfContextMap();
        wrapper.execute(() -> {
            try {
                MDC.setContextMap(contextMap);
                command.run();
            } finally {
                MDC.clear();
            }
        });
    }
}
