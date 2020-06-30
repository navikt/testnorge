package no.nav.registre.populasjoner.kafka;

import static java.util.stream.Collectors.groupingBy;

import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@UtilityClass
public class CollectionUtils {

    public static <T> Collection<List<T>> chunk(
            Collection<T> objects,
            int chunkSize
    ) {
        val counter = new AtomicInteger();
        return objects.stream()
                .collect(groupingBy(it -> counter.getAndIncrement() / chunkSize))
                .values();
    }
}