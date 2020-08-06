package no.nav.dolly;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

public class ForkJoinWorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {

    @Override
    public final ForkJoinWorkerThread newThread(ForkJoinPool pool) {

        return new MyForkJoinWorkerThread(pool);
    }

    private class MyForkJoinWorkerThread extends ForkJoinWorkerThread {

        private MyForkJoinWorkerThread(final ForkJoinPool pool) {
            super(pool);
            // set the correct classloader here
            setContextClassLoader(Thread.currentThread().getContextClassLoader());
        }
    }
}