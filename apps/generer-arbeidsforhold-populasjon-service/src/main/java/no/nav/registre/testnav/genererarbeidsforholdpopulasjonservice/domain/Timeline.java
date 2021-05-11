package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Timeline<T> {
    private final Map<LocalDate, Set<TimelineEntry<T>>> timeline;

    public Timeline() {
        timeline = new HashMap<>();
    }
}
