package no.nav.dolly.bestilling.tpsmessagingservice;

import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.domain.resultset.tpsmessagingservice.bankkonto.NorskBankkontoRequest;
import no.nav.dolly.domain.resultset.tpsmessagingservice.bankkonto.TpsMessagingResponse;
import no.nav.dolly.domain.resultset.tpsmessagingservice.bankkonto.UtenlandskBankkontoRequest;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.nonNull;
import static net.logstash.logback.util.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class TpsMessagingClient implements ClientRegister {

    private final TpsMessagingConsumer tpsMessagingConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        StringBuilder status = new StringBuilder();

        try {
            log.info("Bestilling fra Dolly-frontend: {}", Json.pretty(bestilling));
            if (nonNull(bestilling.getTpsMessaging())) {

                if (!bestilling.getTpsMessaging().getUtenlandskBankkonto().isEmpty()) {
                    ResponseEntity<TpsMessagingResponse> response = sendUtenlandskBankkonto(
                            bestilling,
                            dollyPerson.getHovedperson());

                    appendResponseStatus(response, status);
                }

                if (!bestilling.getTpsMessaging().getNorskBankkonto().isEmpty()) {
                    ResponseEntity<TpsMessagingResponse> response = sendNorskBankkonto(
                            bestilling,
                            dollyPerson.getHovedperson());

                    appendResponseStatus(response, status);
                }
            }
        } catch (RuntimeException e) {
            status.append(errorStatusDecoder.decodeRuntimeException(e));
            log.error("Kall til TPS messaging service feilet: {}", e.getMessage(), e);
        }
        progress.setTpsImportStatus(progress.getTpsImportStatus().isBlank() ? status.toString() : ", " + status);
    }

    @Override
    public void release(List<String> identer) {

        throw new UnsupportedOperationException("Release ikke implementert");
    }


    private ResponseEntity<TpsMessagingResponse> sendUtenlandskBankkonto(RsDollyUtvidetBestilling bestilling, String hovedPerson) {
        return tpsMessagingConsumer.sendUtenlandskBankkontoRequest(
                new UtenlandskBankkontoRequest(
                        hovedPerson,
                        bestilling.getEnvironments(),
                        bestilling.getTpsMessaging().getUtenlandskBankkonto().get(0)));
    }

    private ResponseEntity<TpsMessagingResponse> sendNorskBankkonto(RsDollyUtvidetBestilling bestilling, String hovedPerson) {
        return tpsMessagingConsumer.sendNorskBankkontoRequest(
                new NorskBankkontoRequest(
                        hovedPerson,
                        bestilling.getEnvironments(),
                        bestilling.getTpsMessaging().getNorskBankkonto().get(0)));
    }

    private void appendResponseStatus(ResponseEntity<TpsMessagingResponse> response, StringBuilder status) {

        if (nonNull(response) && response.hasBody()) {
            if (response.getStatusCode().is2xxSuccessful()) {
                status.append(isBlank(status) ? null : ",");
                status.append(response.getBody().miljoe());
                status.append(":");
                status.append(response.getBody().status().equals("OK") ? "OK" : "FEIL: " + response.getBody().utfyllendeMelding());
            } else {
                status.append("FEIL: ").append(response.getBody().utfyllendeMelding());
            }
        } else {
            status.append("Mottok ikke svar fra TPS import");
        }
    }
}
