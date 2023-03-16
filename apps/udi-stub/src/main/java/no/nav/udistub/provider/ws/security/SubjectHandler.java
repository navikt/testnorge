package no.nav.udistub.provider.ws.security;

import lombok.extern.slf4j.Slf4j;

import javax.security.auth.Subject;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import static java.lang.System.getProperty;
@Slf4j
public abstract class SubjectHandler {

    public static final String SUBJECTHANDLER_IMPLEMENTATION_CLASS = "subjectHandlerImplementationClass";

    public static SubjectHandler getSubjectHandler() {

        String subjectHandlerImplementationClass = resolveProperty(SUBJECTHANDLER_IMPLEMENTATION_CLASS);

        try {
            Class<?> clazz = Class.forName(subjectHandlerImplementationClass);
            return (SubjectHandler) clazz.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException("Could not configure subject handler", e);
        }
    }

    private static String resolveProperty(String key) {
        String value = getProperty(key);
        if (value != null) {
            log.debug("Setting " + key + "={} from System.properties", value);
        }
        return value;
    }

    public abstract Subject getSubject();

    public String getConsumerId() {
        if (getSubject() != null) {
            ConsumerId consumerId = getTheOnlyOneInSet(getSubject().getPrincipals(ConsumerId.class));
            if (consumerId != null) {
                return consumerId.getConsumerId();
            }
        }
        return null;
    }

    private <T> T getTheOnlyOneInSet(Set<T> set) {
        if (set.isEmpty()) {
            return null;
        }

        T first = set.iterator().next();
        if (set.size() == 1) {
            return first;
        }

        log.error("expected 1 (or zero) items, got " + set.size() + ", listing them:");
        for (T item : set) {
            log.error(item.toString());
        }
        throw new IllegalStateException("To many (" + set.size() + ") " + first.getClass().getName() + ". Should be either 1 (logged in) og 0 (not logged in)");
    }
}
