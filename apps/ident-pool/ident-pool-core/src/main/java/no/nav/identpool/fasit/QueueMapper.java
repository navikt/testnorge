package no.nav.identpool.fasit;

import static no.nav.identpool.fasit.RegexUtils.QUEUE_NAME_PATTERN;
import static no.nav.identpool.fasit.RegexUtils.getValue;

import org.springframework.stereotype.Component;

@Component
public class QueueMapper implements Mapper {

    @Override
    public boolean supported(Class<?> clazz) {
        return Queue.class.isAssignableFrom(clazz);
    }

    @Override public <T> T map(String json, Class<T> type) {
        String name = getValue(json, QUEUE_NAME_PATTERN);
        return (T) new Queue(name);
    }
}
