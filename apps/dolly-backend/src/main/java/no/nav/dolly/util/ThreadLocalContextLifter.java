package no.nav.dolly.util;

import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import reactor.core.CoreSubscriber;
import reactor.core.Scannable;
import reactor.util.context.Context;

import java.util.Map;
import java.util.function.BiFunction;

public class ThreadLocalContextLifter<T> implements BiFunction<Scannable, CoreSubscriber<? super T>, CoreSubscriber<? super T>> {

    private static Logger logger = LoggerFactory.getLogger(ThreadLocalContextLifter.class);

    private static final ThreadLocal<Context> contextHolder = new ThreadLocal<>();

    public static Context getContext() {
        Context context = contextHolder.get();
        if (context == null) {
            context = Context.empty();
            contextHolder.set(context);
        }
        return context;
    }

    public static void setContext(Context context) {
        contextHolder.set(context);
    }

    @Override
    public CoreSubscriber<? super T> apply(Scannable scannable, CoreSubscriber<? super T> coreSubscriber) {
        return new ThreadLocalContextCoreSubscriber<>(coreSubscriber);
    }

    final class ThreadLocalContextCoreSubscriber<U> implements CoreSubscriber<U> {

        private CoreSubscriber<? super U> delegate;

        public ThreadLocalContextCoreSubscriber(CoreSubscriber<? super U> delegate) {
            this.delegate = delegate;
        }

        @Override
        public Context currentContext() {
            return delegate.currentContext();
        }

        @Override
        public void onSubscribe(Subscription s) {
            delegate.onSubscribe(s);
        }

        @Override
        public void onNext(U u) {
            Context context = delegate.currentContext();
            if (!context.isEmpty()) {
                Context currentContext = ThreadLocalContextLifter.getContext();
                if (!currentContext.equals(context)) {
                    if (context.hasKey("mdc")) {
                        logger.trace("set mdc context to holder {}", context);
                        var mdcContext = (Map<String, String>)context.get("mdc");
                        MDC.setContextMap(mdcContext);
                    } else {
                        var mdcContext = MDC.getCopyOfContextMap();
                        if (mdcContext != null) {
                            context.put("mdc", mdcContext);
                        }
                    }

                    ThreadLocalContextLifter.setContext(context);

                }
            }
            delegate.onNext(u);
        }

        @Override
        public void onError(Throwable t) {
            delegate.onError(t);
        }

        @Override
        public void onComplete() {
            delegate.onComplete();
        }
    }
}
