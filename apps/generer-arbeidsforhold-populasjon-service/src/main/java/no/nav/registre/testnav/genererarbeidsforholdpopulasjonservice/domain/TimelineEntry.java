package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode
public class TimelineEntry<T> {
    private final String key;
    private final T entry;
}
