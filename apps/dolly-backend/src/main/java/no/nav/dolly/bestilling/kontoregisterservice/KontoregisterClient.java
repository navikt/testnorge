package no.nav.dolly.bestilling.kontoregisterservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getVarsel;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class KontoregisterClient implements ClientRegister {
    private final KontoregisterConsumer kontoregisterConsumer;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getBankkonto())) {

            if (!dollyPerson.isOpprettetIPDL()) {
                progress.setKontoregisterStatus(encodeStatus(getVarsel("Kontoregister")));
                return;
            }
            if (nonNull(bestilling.getBankkonto().getNorskBankkonto())) {
                progress.setKontoregisterStatus(
                        kontoregisterConsumer
                                .sendNorskBankkontoRequest(dollyPerson.getHovedperson(), bestilling.getBankkonto().getNorskBankkonto())
                                .block()
                );
            }
            if (nonNull(bestilling.getBankkonto().getUtenlandskBankkonto())) {
                progress.setKontoregisterStatus(
                        kontoregisterConsumer
                                .sendUtenlandskBankkontoRequest(dollyPerson.getHovedperson(), bestilling.getBankkonto().getUtenlandskBankkonto())
                                .block()
                );
            }
        } else if (nonNull(bestilling.getTpsMessaging())) {
            if (nonNull(bestilling.getTpsMessaging().getNorskBankkonto())) {
                progress.setKontoregisterStatus(
                        kontoregisterConsumer
                                .sendNorskBankkontoRequest(dollyPerson.getHovedperson(), bestilling.getTpsMessaging().getNorskBankkonto())
                                .block()
                );
            }
            if (nonNull(bestilling.getTpsMessaging().getUtenlandskBankkonto())) {
                progress.setKontoregisterStatus(
                        kontoregisterConsumer
                                .sendUtenlandskBankkontoRequest(dollyPerson.getHovedperson(), bestilling.getTpsMessaging().getUtenlandskBankkonto())
                                .block()
                );
            }
        }
    }

    @Override
    public void release(List<String> identer) {
        kontoregisterConsumer.slettKontoer(identer)
                .subscribe(response -> log.info("Slettet kontoer fra Kontoregister"));
    }

    @Override
    public boolean isDone(RsDollyBestilling kriterier, Bestilling bestilling) {

        return isNull(kriterier.getBankkonto()) ||
                bestilling.getProgresser().stream()
                        .allMatch(entry -> isNotBlank(entry.getKontoregisterStatus()));
    }
}
