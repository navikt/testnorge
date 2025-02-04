package no.nav.dolly.bestilling.arbeidssoekerregistrering.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.arbeidssoekerregistrering.dto.ArbeidssokerregistreringResponse;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class LagreArbeidsoekerregister implements Callable<Mono<ArbeidssokerregistreringResponse>> {

    @Override
    public Mono<ArbeidssokerregistreringResponse> call() {


        return null;
    }
}
