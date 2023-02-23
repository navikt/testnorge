package no.nav.dolly.bestilling.arbeidsplassencv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsplassenCVClient implements ClientRegister {

    private final ArbeidplassenCVConsumer arbeidplassenCVConsumer;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getArbeidsplassenCV())) {

        }
        return Flux.empty();
    }

    @Override
    public void release(List<String> identer) {

        arbeidplassenCVConsumer.deleteCVer(identer)
                .collectList()
                .subscribe(response -> log.info("Slettet utført mot ArbeidsplassenCV"));
    }
}
