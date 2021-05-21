package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class Timeline<T extends Id> {
    private final Map<LocalDate, TimelineEntries<T>> timeline;
    private final Map<LocalDate, TimelineEntries<T>> history;

    public Timeline(Map<LocalDate, T> map) {
        timeline = new HashMap<>();
        history = new HashMap<>();
        map.forEach(this::put);
    }


    public void put(LocalDate date, T value) {
        if (!timeline.containsKey(date)) {
            timeline.put(date, new TimelineEntries<>());
        }

        var timelineEntries = timeline.get(date);
        timelineEntries.put(value);
    }

    public void replace(LocalDate date, T value) {
        if (timeline.containsKey(date)) {
            history.put(date, timeline.get(date));
        }

        timeline.put(date, new TimelineEntries<>());
        var timelineEntries = timeline.get(date);
        timelineEntries.put(value);
    }

    public void update(Timeline<T> timeline) {
        timeline.forEach((date, entries) -> entries.forEach(value -> this.replace(date, value)));
    }

    public void forEach(BiConsumer<LocalDate, Set<T>> action) {
        timeline.forEach((date, entries) -> action.accept(date, entries.getAll()));
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
