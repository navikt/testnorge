package no.nav.dolly.bestilling.tpsmessagingservice;

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

@Slf4j
@Service
@Order(7)
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
            if (nonNull(bestilling.getTpsMessaging().getSpraakKode())) {
                appendResponseStatus(
                        tpsMessagingConsumer.sendSpraakkodeRequest(
                                dollyPerson.getHovedperson(),
                                bestilling.getEnvironments(),
                                mapperFacade.map(bestilling.getTpsMessaging().getSpraakKode(), SpraakDTO.class)),
                        status,
                        "SprakKode_opprett"
                );
            }

            if (!bestilling.getTpsMessaging().getSikkerhetstiltak().isEmpty()) {
                var sikkerhetstiltakStatus = tpsMessagingConsumer.deleteSikkerhetstiltakRequest(
                        dollyPerson.getHovedperson(),
                        bestilling.getEnvironments());

                if (sikkerhetstiltakStatus.stream()
                        .anyMatch(resultat -> !resultat.getUtfyllendeMelding().contains("Opphør på ikke eksist. sikkerhet"))) {
                    appendResponseStatus(sikkerhetstiltakStatus.stream()
                                    .filter(result -> !result.getUtfyllendeMelding().contains("Opphør på ikke eksist. sikkerhet"))
                                    .toList(),
                            status,
                            "Sikkerhetstiltak_slett"
                    );
                }
                appendResponseStatus(
                        tpsMessagingConsumer.sendSikkerhetstiltakRequest(
                                dollyPerson.getHovedperson(),
                                bestilling.getEnvironments(),
                                bestilling.getTpsMessaging().getSikkerhetstiltak().get(0)),
                        status,
                        "Sikkerhetstiltak_opprett"
                );
            }

            if (nonNull(bestilling.getTpsMessaging().getEgenAnsattDatoFom())) {
                appendResponseStatus(
                        tpsMessagingConsumer.sendEgenansattRequest(
                                dollyPerson.getHovedperson(),
                                bestilling.getEnvironments(),
                                bestilling.getTpsMessaging().getEgenAnsattDatoFom()),
                        status,
                        "Egenansatt_opprett"
                );
            }
            if (nonNull(bestilling.getTpsMessaging().getEgenAnsattDatoTom()) &&
                    !bestilling.getTpsMessaging().getEgenAnsattDatoTom().isAfter(LocalDate.now())) {
                appendResponseStatus(
                        tpsMessagingConsumer.deleteEgenansattRequest(
                                dollyPerson.getHovedperson(),
                                bestilling.getEnvironments()),
                        status,
                        "Egenansatt_slett"
                );
            }

            if (!bestilling.getTpsMessaging().getTelefonnummer().isEmpty()) {
                var tlfStatus = tpsMessagingConsumer.deleteTelefonnummerRequest(
                        dollyPerson.getHovedperson(),
                        bestilling.getEnvironments());

                if (tlfStatus.stream()
                        .anyMatch(resultat -> !resultat.getUtfyllendeMelding().contains("ingen aktiv telefonr funnet"))) {

                    appendResponseStatus(tlfStatus.stream()
                                    .filter(result -> !result.getUtfyllendeMelding().contains("ingen aktiv telefonr funnet"))
                                    .toList(),
                            status,
                            "Telefonnummer_slett"
                    );
                }
                appendResponseStatus(
                        tpsMessagingConsumer.sendTelefonnummerRequest(
                                dollyPerson.getHovedperson(),
                                bestilling.getEnvironments(),
                                bestilling.getTpsMessaging().getTelefonnummer()),
                        status,
                        "Telefonnummer_opprett"
                );
            }

            sendBankkontoer(bestilling, dollyPerson, status);

        } catch (RuntimeException e) {
            progress.setFeil(errorStatusDecoder.decodeRuntimeException(e));
            log.error("Kall til TPS messaging service feilet: {}", e.getMessage(), e);
        }
        progress.setTpsMessagingStatus(status.toString());
    }

    @Override
    public void release(List<String> identer) {

        // TpsMessaging har ikke sletting
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

    private void appendResponseStatus(List<TpsMeldingResponseDTO> responseList, StringBuilder status, String melding) {

        status.append('$')
                .append(melding)
                .append('#');
        responseList.forEach(response -> {
            status.append(response.getMiljoe());
            status.append(':');
            status.append("OK".equals(response.getStatus()) ? "OK" : "FEIL= " + response.getUtfyllendeMelding());
            status.append(',');
        });
    }
}
