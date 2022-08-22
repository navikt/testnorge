package no.nav.testnav.apps.syntvedtakshistorikkservice.config;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

public class SyntForkJoinWorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {

    @Override
    public final ForkJoinWorkerThread newThread(ForkJoinPool pool) {

        return new MyForkJoinWorkerThread(pool);
    }

    private static final class MyForkJoinWorkerThread extends ForkJoinWorkerThread {

        private MyForkJoinWorkerThread(final ForkJoinPool pool) {
            super(pool);
            // set the correct classloader here
            setContextClassLoader(Thread.currentThread().getContextClassLoader());
        }
    }
}