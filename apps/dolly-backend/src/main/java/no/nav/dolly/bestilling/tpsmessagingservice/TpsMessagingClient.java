package no.nav.dolly.bestilling.tpsmessagingservice;

import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.SpraakDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@Order(6)
@RequiredArgsConstructor
public class TpsMessagingClient implements ClientRegister {

    private final TpsMessagingConsumer tpsMessagingConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (isNull(bestilling.getTpsMessaging())) {
            return;
        }
        StringBuilder status = new StringBuilder();

        try {
            log.info("Bestilling fra Dolly-frontend: {}", Json.pretty(bestilling));

            if (nonNull(bestilling.getTpsMessaging().getSpraakKode())) {
                appendResponseStatus(
                        tpsMessagingConsumer.sendSpraakkodeRequest(
                                dollyPerson.getHovedperson(),
                                bestilling.getEnvironments(),
                                mapperFacade.map(bestilling.getTpsMessaging().getSpraakKode(), SpraakDTO.class)),
                        status,
                        "SprakKode"
                );
            }

            if (nonNull(bestilling.getTpsMessaging().getEgenAnsattDatoFom())) {
                appendResponseStatus(
                        tpsMessagingConsumer.sendEgenansattRequest(
                                dollyPerson.getHovedperson(),
                                bestilling.getEnvironments(),
                                bestilling.getTpsMessaging().getEgenAnsattDatoFom()),
                        status,
                        "Egenansatt_send"
                );
            }
            if (nonNull(bestilling.getTpsMessaging().getEgenAnsattDatoTom()) &&
                    !bestilling.getTpsMessaging().getEgenAnsattDatoTom().isAfter(LocalDate.now())) {
                appendResponseStatus(
                        tpsMessagingConsumer.deleteEgenansattRequest(
                                dollyPerson.getHovedperson(),
                                bestilling.getEnvironments()),
                        status,
                        "Egenansatt_delete"
                );
            }

            if (nonNull(bestilling.getTpsMessaging().getTelefonnummer())) {
                // TODO + sjekk om person har telefonnr?
                var tlfStatus = tpsMessagingConsumer.deleteTelefonnummerRequest(
                        dollyPerson.getHovedperson(),
                        bestilling.getEnvironments());

                appendResponseStatus(tlfStatus.stream()
                                .filter(result -> !result.getUtfyllendeMelding().contains("ingen aktiv telefonr funnet") &&
                                        !result.getUtfyllendeMelding().contains("ei opphør"))
                                .toList(),
                        status,
                        "Telefonnummer_delete"
                );
            }

            if (nonNull(bestilling.getTpsMessaging().getTelefonnummer())) {
                appendResponseStatus(
                        tpsMessagingConsumer.sendTelefonnummerRequest(
                                dollyPerson.getHovedperson(),
                                bestilling.getEnvironments(),
                                bestilling.getTpsMessaging().getTelefonnummer()),
                        status,
                        "Telefonnummer_send"
                );
            }

            sendBankkontoer(bestilling, dollyPerson, status);

        } catch (RuntimeException e) {
            status.append(errorStatusDecoder.decodeRuntimeException(e));
            log.error("Kall til TPS messaging service feilet: {}", e.getMessage(), e);
        }
        progress.setTpsMessagingStatus(status.toString());
    }

    @Override
    public void release(List<String> identer) {

        throw new UnsupportedOperationException("Release ikke implementert");
    }

    private void sendBankkontoer(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, StringBuilder status) {
        if (nonNull(bestilling.getTpsMessaging().getUtenlandskBankkonto())) {

            appendResponseStatus(tpsMessagingConsumer.sendUtenlandskBankkontoRequest(
                            dollyPerson.getHovedperson(),
                            bestilling.getEnvironments(),
                            bestilling.getTpsMessaging().getUtenlandskBankkonto()),
                    status, "UtenlandskBankkonto");
        }

        if (nonNull(bestilling.getTpsMessaging().getNorskBankkonto())) {

            appendResponseStatus(tpsMessagingConsumer.sendNorskBankkontoRequest(
                            dollyPerson.getHovedperson(),
                            bestilling.getEnvironments(),
                            bestilling.getTpsMessaging().getNorskBankkonto()),
                    status, "NorskBankkonto");
        }
    }

    private void appendResponseStatus(List<TpsMeldingResponseDTO> responseList, StringBuilder status, String enhet) {

        responseList.forEach(response -> {
            if (isNotBlank(status)) {
                status.append(",");
            }
            status.append(enhet).append("#");
            status.append(enhet).append("-");
            status.append(response.getMiljoe());
            status.append(":");
            status.append(response.getStatus().equals("OK") ? "OK" : "FEIL:" + enhet + "-" + response.getUtfyllendeMelding());
        });
    }
}
