package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.adapter;

import reactor.core.publisher.Flux;

import java.util.Set;

public interface IdentAdapter {
    Flux<String> getIdenter(String miljo, int antall);
}
