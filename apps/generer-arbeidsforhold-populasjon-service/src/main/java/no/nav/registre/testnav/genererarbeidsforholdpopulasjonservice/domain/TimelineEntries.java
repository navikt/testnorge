package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class TimelineEntries<T extends Id> {
    private final Map<String, T> entires;

    public TimelineEntries() {
        entires = new TreeMap<>();
    }

    public void put(T value) {
        entires.put(value.getId(), value);
    }

    public Set<T> getAll(){
        return entires.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toSet());
    }
}
