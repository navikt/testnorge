package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding.Arbeidsforhold;

public class Person {
    private final String ident;
    private final Timeline<Arbeidsforhold> timeline;

    public Person(String ident) {
        this.ident = ident;
        this.timeline = new Timeline<>();
    }
}
