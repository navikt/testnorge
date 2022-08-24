package no.nav.dolly.bestilling.kontoregisterservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class KontoregisterClient implements ClientRegister {
    private final KontoregisterConsumer kontoregisterConsumer;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {
        if (nonNull(bestilling.getBankkonto())) {
            if (nonNull(bestilling.getBankkonto().getNorskBankkonto())) {
                updateProgress(progress, kontoregisterConsumer.sendNorskBankkontoRequest(dollyPerson.getHovedperson(), bestilling.getBankkonto().getNorskBankkonto()));
            }
            if (nonNull(bestilling.getBankkonto().getUtenlandskBankkonto())) {
                updateProgress(progress, kontoregisterConsumer.sendUtenlandskBankkontoRequest(dollyPerson.getHovedperson(), bestilling.getBankkonto().getUtenlandskBankkonto()));
            }
        } else if (nonNull(bestilling.getTpsMessaging())) {
            if (nonNull(bestilling.getTpsMessaging().getNorskBankkonto())) {
                updateProgress(progress, kontoregisterConsumer.sendNorskBankkontoRequest(dollyPerson.getHovedperson(), bestilling.getTpsMessaging().getNorskBankkonto()));
            }
            if (nonNull(bestilling.getTpsMessaging().getUtenlandskBankkonto())) {
                updateProgress(progress, kontoregisterConsumer.sendUtenlandskBankkontoRequest(dollyPerson.getHovedperson(), bestilling.getTpsMessaging().getUtenlandskBankkonto()));
            }
        }
    }

    private void updateProgress(BestillingProgress progress, Mono<Void> request) {
        try {
            request.block();
            log.info("bestilling {} med kontoregister gikk Ok", progress.getBestilling().getId());
            progress.setKontoregisterStatus("OK");
        } catch(Exception e) {
            log.error("bestilling {} med kontoregister fikk feil {}", progress.getBestilling().getId(), e.getMessage());
            progress.setKontoregisterStatus("Feil= " + e.getMessage());
        }
    }

    @Override
    public void release(List<String> identer) {
        // Kontoregister har ikke sletting
    }
}
