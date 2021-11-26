package no.nav.dolly.bestilling.tpsmessagingservice;

import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.domain.resultset.tpsmessagingservice.utenlandskbankkonto.NorskBankkontoRequest;
import no.nav.dolly.domain.resultset.tpsmessagingservice.utenlandskbankkonto.UtenlandskBankkontoRequest;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.ResponseHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class TpsMessagingClient implements ClientRegister {

    private final TpsMessagingConsumer tpsMessagingConsumer;
    private final ResponseHandler responseHandler;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        try {
            log.info("Bestilling fra Dolly-frontend: {}", Json.pretty(bestilling));
            if (nonNull(bestilling.getTpsMessaging())) {


                if (!bestilling.getTpsMessaging().getUtenlandskBankkonto().isEmpty()) {
                    sendUtenlandskBankkonto(
                            bestilling,
                            dollyPerson.getHovedperson(),
                            progress);
                }

                if (!bestilling.getTpsMessaging().getNorskBankkonto().isEmpty()) {
                    sendNorskBankkonto(
                            bestilling,
                            dollyPerson.getHovedperson(),
                            progress);
                }
            }
        } catch (RuntimeException e) {
            progress.setTpsImportStatus(errorStatusDecoder.decodeRuntimeException(e));
            log.error("Kall til TPS messaging service feilet: {}", e.getMessage(), e);
        }
    }

    @Override
    public void release(List<String> identer) {

        throw new UnsupportedOperationException("Release ikke implementert");
    }

    private void sendUtenlandskBankkonto(RsDollyUtvidetBestilling bestilling, String hovedPerson, BestillingProgress progress) {
        ResponseEntity<Object> tpsMessagingResponse = tpsMessagingConsumer.sendUtenlandskBankkontoRequest(
                new UtenlandskBankkontoRequest(
                        hovedPerson,
                        bestilling.getEnvironments(),
                        bestilling.getTpsMessaging().getUtenlandskBankkonto().get(0)));
        progress.setTpsImportStatus(responseHandler.extractResponse(tpsMessagingResponse));

    }

    private void sendNorskBankkonto(RsDollyUtvidetBestilling bestilling, String hovedPerson, BestillingProgress progress) {
        ResponseEntity<Object> tpsMessagingResponse = tpsMessagingConsumer.sendNorskBankkontoRequest(
                new NorskBankkontoRequest(
                        hovedPerson,
                        bestilling.getEnvironments(),
                        bestilling.getTpsMessaging().getNorskBankkonto().get(0)));
        progress.setTpsImportStatus(responseHandler.extractResponse(tpsMessagingResponse));
    }
}
