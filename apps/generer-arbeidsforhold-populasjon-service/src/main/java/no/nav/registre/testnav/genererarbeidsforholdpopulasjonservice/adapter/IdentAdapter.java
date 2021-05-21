package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.adapter;

import reactor.core.publisher.Flux;

public interface IdentAdapter {
    Flux<String> getIdenter(String miljo, int antall);
}
