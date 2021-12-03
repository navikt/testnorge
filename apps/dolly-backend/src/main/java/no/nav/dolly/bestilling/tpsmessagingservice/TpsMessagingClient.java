package no.nav.dolly.bestilling.tpsmessagingservice;

import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.domain.resultset.tpsmessagingservice.NorskBankkontoRequest;
import no.nav.dolly.domain.resultset.tpsmessagingservice.UtenlandskBankkontoRequest;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;
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
            log.trace("Bestilling fra Dolly-frontend: {}", Json.pretty(bestilling));
            if (nonNull(bestilling.getTpsMessaging())) {

                if (nonNull(bestilling.getTpsMessaging().getUtenlandskBankkonto())) {
                    appendResponseStatus(sendUtenlandskBankkonto(
                            bestilling,
                            dollyPerson.getHovedperson()), status);
                }

                if (nonNull(bestilling.getTpsMessaging().getNorskBankkonto())) {
                    appendResponseStatus(sendNorskBankkonto(
                            bestilling,
                            dollyPerson.getHovedperson()), status);
                }
            }
        } catch (RuntimeException e) {
            status.append(errorStatusDecoder.decodeRuntimeException(e));
            log.error("Kall til TPS messaging service feilet: {}", e.getMessage(), e);
        }
        progress.setTpsImportStatus(isNull(progress.getTpsImportStatus()) || progress.getTpsImportStatus().isBlank()
                ? status.toString()
                : progress.getTpsImportStatus() + ", " + status);
    }

    @Override
    public void release(List<String> identer) {

        throw new UnsupportedOperationException("Release ikke implementert");
    }


    private List<TpsMeldingResponseDTO> sendUtenlandskBankkonto(RsDollyUtvidetBestilling bestilling, String hovedPerson) {

        return tpsMessagingConsumer.sendUtenlandskBankkontoRequest(
                new UtenlandskBankkontoRequest(
                        hovedPerson,
                        bestilling.getEnvironments(),
                        bestilling.getTpsMessaging().getUtenlandskBankkonto()));
    }

    private List<TpsMeldingResponseDTO> sendNorskBankkonto(RsDollyUtvidetBestilling bestilling, String hovedPerson) {

        return tpsMessagingConsumer.sendNorskBankkontoRequest(
                new NorskBankkontoRequest(
                        hovedPerson,
                        bestilling.getEnvironments(),
                        bestilling.getTpsMessaging().getNorskBankkonto()));
    }

    private void appendResponseStatus(List<TpsMeldingResponseDTO> responseList, StringBuilder status) {

        responseList.forEach(response -> {
            status.append(isBlank(status) ? null : ",");
            status.append(response.getMiljoe());
            status.append(":");
            status.append(response.getStatus().equals("OK") ? "OK" : "FEIL:" + response.getUtfyllendeMelding());
        });
    }
}
