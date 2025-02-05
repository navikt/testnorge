package no.nav.dolly.bestilling.arbeidssoekerregisteret;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class ArbeidssoekerregisteretClient implements ClientRegister {

    private final ArbeidssoekerregisteretConsumer arbeidssoekerregisteretConsumer;


    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        var test = Flux.just(bestilling)
                .filter(bestilling1 -> nonNull(bestilling1.getArbeidssokerregisteret()))
                .map()
            return Flux.just(dollyPerson.getIdent())
                    .map(person -> buildRequest(bestilling.getArbeidssokerregisteret(), person
        return null;
    }

    @Override
    public void release(List<String> identer) {

    }
}
