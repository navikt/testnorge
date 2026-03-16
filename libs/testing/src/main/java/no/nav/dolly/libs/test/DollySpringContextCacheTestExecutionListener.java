package no.nav.dolly.libs.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.NonNull;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.cache.ContextCache;
import org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.Objects.isNull;

@Slf4j
class DollySpringContextCacheTestExecutionListener extends AbstractTestExecutionListener {

    private static final ConcurrentMap<Class<?>, CacheSnapshot> CACHE_SNAPSHOTS = new ConcurrentHashMap<>();

    @Override
    public void beforeTestClass(@NonNull TestContext testContext) {
        var testClass = testContext.getTestClass();
        var contextCache = resolveContextCacheBasedOnAnnotation(testClass);
        if (isNull(contextCache)) {
            return;
        }
        CACHE_SNAPSHOTS.put(testClass, CacheSnapshot.from(contextCache));
    }

    @Override
    public void afterTestClass(@NonNull TestContext testContext) {

        var testClass = testContext.getTestClass();
        var contextCache = resolveContextCacheBasedOnAnnotation(testClass);
        if (isNull(contextCache)) {
            return;
        }
        var before = CACHE_SNAPSHOTS.remove(testClass);
        if (isNull(before)) {
            return;
        }

        var after = CacheSnapshot.from(contextCache);
        var sizeDelta = after.size() - before.size();
        var missDelta = after.missCount() - before.missCount();
        var hitDelta = after.hitCount() - before.hitCount();
        var dirtiesContext = testClass.isAnnotationPresent(DirtiesContext.class);

        if (sizeDelta > 0) {
            log.info("spring-context-cache-growth testClass={} sizeBefore={} sizeAfter={} delta={} missDelta={} hitDelta={} dirtiesContext={}",
                    testClass.getName(),
                    before.size(),
                    after.size(),
                    sizeDelta,
                    missDelta,
                    hitDelta,
                    dirtiesContext);
        } else {
            log.debug("spring-context-cache-usage testClass={} sizeBefore={} sizeAfter={} delta={} missDelta={} hitDelta={} dirtiesContext={}",
                    testClass.getName(),
                    before.size(),
                    after.size(),
                    sizeDelta,
                    missDelta,
                    hitDelta,
                    dirtiesContext);
        }

    }

    private static ContextCache resolveContextCacheBasedOnAnnotation(Class<?> testClass) {
        if (AnnotatedElementUtils.hasAnnotation(testClass, DollyTrackSpringContextCache.class)) {
            try {
                var defaultContextCacheField = DefaultCacheAwareContextLoaderDelegate.class.getDeclaredField("defaultContextCache");
                defaultContextCacheField.setAccessible(true);
                var cache = defaultContextCacheField.get(null);
                if (cache instanceof ContextCache contextCache) {
                    return contextCache;
                }
            } catch (Exception e) {
                log.debug("Unable to resolve Spring Test context cache", e);
            }
        }
        return null;

    }

    private record CacheSnapshot(int size, int hitCount, int missCount) {
        private static CacheSnapshot from(ContextCache contextCache) {
            return new CacheSnapshot(
                    contextCache.size(),
                    contextCache.getHitCount(),
                    contextCache.getMissCount()
            );
        }
    }

}
