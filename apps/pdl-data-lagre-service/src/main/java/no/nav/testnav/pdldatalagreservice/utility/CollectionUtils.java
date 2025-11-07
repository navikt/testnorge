package no.nav.testnav.pdldatalagreservice.utility;

import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@UtilityClass
public class CollectionUtils {

    public static <T, V> Collector<T, ?, List<T>> deduplicating(Function<T, V> keyExtractor) {
        return Collectors.collectingAndThen(
                toMap(keyExtractor, Function.identity(), (first, second) -> second),
                map -> map.values().stream().toList());
    }

    public static <T> Collection<List<T>> chunk(Collection<T> objects, int chunkSize) {
        val counter = new AtomicInteger();
        return objects.stream()
                .collect(groupingBy(it -> counter.getAndIncrement() / chunkSize))
                .values();
    }

    public static <T> Collector<T, ?, T> toSingleton() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() != 1) {
                        throw new IllegalStateException(String.format("Expected list size: 1. Got: %s", list.size()));
                    }
                    return list.getFirst();
                }
        );
    }

    public static <T> Collector<T, ?, Optional<T>> toOptionalSingleton() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() > 1) {
                        throw new IllegalStateException();
                    } else if (list.isEmpty()) {
                        return Optional.empty();
                    } else {
                        return Optional.ofNullable(list.getFirst());
                    }
                }
        );
    }

    public static <T> Collector<T, ?, T> toSingletonOrThrow(RuntimeException exception) {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() != 1) {
                        throw exception;
                    }
                    return list.getFirst();
                }
        );
    }

    public static <T, E extends RuntimeException> BinaryOperator<T> toSingletonOrThrow(Supplier<E> exception) {
        return (element, otherElement) -> {
            throw exception.get();
        };
    }

    public static <T> Collector<T, ?, List<T>> toReversedList() {
        return Collectors.collectingAndThen(Collectors.toList(), l -> {
            Collections.reverse(l);
            return l;
        });
    }

    public static <T> List<T> iterableToList(Iterable<T> iterator) {
        return StreamSupport.stream(iterator.spliterator(), false).toList();
    }

    public static <T> List<T> combineLists(List<T> list1, List<T> list2) {
        return Stream.of(list1, list2)
                .flatMap(Collection::stream)
                .toList();
    }

    public static <T> BinaryOperator<T> onlyUseLast() {
        return (first, second) -> second;
    }
}