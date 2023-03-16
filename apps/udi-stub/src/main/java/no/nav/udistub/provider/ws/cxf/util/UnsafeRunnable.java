package no.nav.udistub.provider.ws.cxf.util;

import lombok.SneakyThrows;

@FunctionalInterface
public interface UnsafeRunnable extends Runnable{

    void runUnsafe() throws Throwable;

    @Override
    @SneakyThrows
    default void run() {
        runUnsafe();
    }
}
