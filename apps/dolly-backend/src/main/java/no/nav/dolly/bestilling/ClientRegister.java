package no.nav.dolly.bestilling;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ClientRegister {

    Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre);

    void release(List<String> identer);

    boolean isDone(RsDollyBestilling kriterier, Bestilling bestilling);
}
