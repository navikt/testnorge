package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.adapter;

import reactor.core.publisher.Flux;

public interface IdentAdapter {
    Flux<String> getIdenter(String miljo, int antall);
}
