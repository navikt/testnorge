package no.nav.dolly.bestilling.arbeidssoekerregisteret.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.arbeidssoekerregisteret.dto.ArbeidssokerregisteretResponse;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class LagreTilArbeidsoekerregisteret implements Callable<Mono<ArbeidssokerregisteretResponse>> {

    @Override
    public Mono<ArbeidssokerregisteretResponse> call() {


        return null;
    }
}
