package no.nav.dolly.bestilling;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ClientRegister {

    Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress);

    void release(List<String> identer);
}
