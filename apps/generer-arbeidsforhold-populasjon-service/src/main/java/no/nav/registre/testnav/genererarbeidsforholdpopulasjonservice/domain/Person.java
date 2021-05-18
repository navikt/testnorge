package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding.Arbeidsforhold;

@Value
@NoArgsConstructor(force = true)
public class Person {
    private final String ident;
    @JsonIgnore
    private final Timeline<Arbeidsforhold> timeline;

    public Person(String ident, Timeline<Arbeidsforhold> timeline) {
        this.ident = ident;
        this.timeline = timeline;
    }

    public String getIdent() {
        return ident;
    }

    public Set<Arbeidsforhold> getArbeidsforholdOn(LocalDate date) {
        return timeline.get(date);
    }

    public Set<Arbeidsforhold> getArbeidsforholdToRemoveOn(LocalDate date) {
        var history = timeline.getHistory(date);
        var current = timeline.get(date);
        return history.stream().filter(value -> !current.contains(value)).collect(Collectors.toSet());
    }

    public void updateTimeline(Timeline<Arbeidsforhold> timeline) {
        this.timeline.update(timeline);
    }


}
