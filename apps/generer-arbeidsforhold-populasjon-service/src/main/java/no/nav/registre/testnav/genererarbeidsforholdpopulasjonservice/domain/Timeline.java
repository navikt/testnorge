package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Timeline<T extends Id> {
    private final Map<LocalDate, TimelineEntries<T>> timeline;
    private final Map<LocalDate, TimelineEntries<T>> history;
    private final Set<LocalDate> updatedDates = new HashSet<>();

    public Timeline(Map<LocalDate, ? extends Collection<T>> map) {
        timeline = new TreeMap<>();
        history = new TreeMap<>();
        map.forEach((key, values) -> values.forEach(value -> put(key, value, false)));
    }

    public Timeline() {
        timeline = new TreeMap<>();
        history = new TreeMap<>();
    }

    public LocalDate getLastDate() {
        return timeline.keySet().stream().reduce(null, (sub, date) -> {
                    if (sub == null) {
                        return date;
                    }
                    return date.compareTo(sub) > 0 ? date : sub;
                }
        );
    }

    public void put(LocalDate date, T value, boolean updated) {
        if(updated){
            updatedDates.add(date);
        }

        if (!timeline.containsKey(date)) {
            timeline.put(date, new TimelineEntries<>());
        }

        var timelineEntries = timeline.get(date);
        timelineEntries.put(value);
    }

    public void put(LocalDate date, T value) {
        put(date, value, true);
    }

    public void replace(LocalDate date, Collection<T> values) {
        updatedDates.add(date);

        if (timeline.containsKey(date)) {
            history.put(date, timeline.get(date));
        }

        timeline.put(date, new TimelineEntries<>());
        var timelineEntries = timeline.get(date);
        values.forEach(timelineEntries::put);
    }

    public void update(Timeline<T> timeline) {
        timeline.forEach(this::replace);
    }

    public void forEach(BiConsumer<LocalDate, Set<T>> action) {
        timeline.forEach((date, entries) -> action.accept(date, entries.getAll()));
    }

    public Set<LocalDate> getUpdatedDates(){
        return updatedDates;
    }

    public <I> List<I> applyForAll(Function<T, I> function) {
        return timeline.values().stream().flatMap(value -> value.getAll().stream().map(function)).collect(Collectors.toList());
    }

    public Set<T> getHistory(LocalDate date) {
        if (!history.containsKey(date)) {
            return Collections.emptySet();
        }
        return history.get(date).getAll();
    }

    public Set<T> get(LocalDate date) {
        if (!timeline.containsKey(date)) {
            return Collections.emptySet();
        }
        return timeline.get(date).getAll();
    }
}
