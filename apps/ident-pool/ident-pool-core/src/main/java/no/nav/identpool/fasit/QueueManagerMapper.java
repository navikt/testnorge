package no.nav.identpool.fasit;

import static no.nav.identpool.fasit.RegexUtils.HOSTNAME_PATTERN;
import static no.nav.identpool.fasit.RegexUtils.NAME_PATTERN;
import static no.nav.identpool.fasit.RegexUtils.PORT_PATTERN;
import static no.nav.identpool.fasit.RegexUtils.getValue;

import org.springframework.stereotype.Component;

@Component
public class QueueManagerMapper implements Mapper {

    @Override
    public boolean supported(Class<?> clazz) {
        return QueueManager.class.isAssignableFrom(clazz);
    }

    @Override
    public <T> T map(String json, Class<T> type) {
        String name = getValue(json, NAME_PATTERN);
        String hostname = getValue(json, HOSTNAME_PATTERN);
        String port = getValue(json, PORT_PATTERN);
        return (T) new QueueManager(name, hostname, port);
    }
}