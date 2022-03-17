package no.nav.dolly.bestilling.tpsmessagingservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.AdresseUtlandDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.SpraakDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TelefonTypeNummerDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@Order(6)
@RequiredArgsConstructor
public class TpsMessagingClient implements ClientRegister {

    private final TpsMessagingConsumer tpsMessagingConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final PdlDataConsumer pdlDataConsumer;

    private static void appendResponseStatus(List<TpsMeldingResponseDTO> responseList, StringBuilder status, String melding) {

        if (!responseList.isEmpty()) {
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

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        var status = new StringBuilder();

        try {
            if (isNull(dollyPerson.getPdlfPerson())) {

                dollyPerson.setPdlfPerson(pdlDataConsumer.getPersoner(List.of(dollyPerson.getHovedperson()))
                        .stream().findFirst().orElse(null));
            }

            sendSpraakkode(bestilling, dollyPerson, status);
            sendBankkontoer(bestilling, dollyPerson, status);
            sendEgenansatt(bestilling, dollyPerson, status);
            sendSikkerhetstiltak(dollyPerson, status);
            sendTelefonnumre(dollyPerson, status);
            sendBostedsadresseUtland(dollyPerson, status);
            sendKontaktadresseUtland(dollyPerson, status);

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

    private void sendBostedsadresseUtland(DollyPerson dollyPerson, StringBuilder status) {
        if (nonNull(dollyPerson.getPdlfPerson())) {

            var responser =
                    Stream.of(List.of(dollyPerson.getPdlfPerson().getPerson()),
                                    dollyPerson.getPdlfPerson().getRelasjoner().stream()
                                            .map(FullPersonDTO.RelasjonDTO::getRelatertPerson)
                                            .toList())
                            .flatMap(Collection::stream)
                            .filter(person -> person.getBostedsadresse().stream().anyMatch(AdresseDTO::isAdresseUtland))
                            .map(person ->
                                    tpsMessagingConsumer.sendAdresseUtlandRequest(person.getIdent(), null,
                                            mapperFacade.map(person.getBostedsadresse().stream()
                                                    .filter(AdresseDTO::isAdresseUtland)
                                                    .findFirst().get(), AdresseUtlandDTO.class)))
                            .toList();

            appendResponseStatus(responser.stream().findFirst().orElse(emptyList()), status, "AdresseUtland_opprett");
        }
    }

    private void sendKontaktadresseUtland(DollyPerson dollyPerson, StringBuilder status) {
        if (nonNull(dollyPerson.getPdlfPerson())) {

            var responser =
                    Stream.of(List.of(dollyPerson.getPdlfPerson().getPerson()),
                                    dollyPerson.getPdlfPerson().getRelasjoner().stream()
                                            .map(FullPersonDTO.RelasjonDTO::getRelatertPerson)
                                            .toList())
                            .flatMap(Collection::stream)
                            .filter(person -> person.getKontaktadresse().stream().anyMatch(AdresseDTO::isAdresseUtland))
                            .map(person ->
                                    tpsMessagingConsumer.sendAdresseUtlandRequest(person.getIdent(), null,
                                            mapperFacade.map(person.getKontaktadresse().stream()
                                                    .filter(AdresseDTO::isAdresseUtland)
                                                    .findFirst().get(), AdresseUtlandDTO.class)))
                            .toList();

            appendResponseStatus(responser.stream().findFirst().orElse(emptyList()), status, "AdresseUtland_opprett");
        }
    }

    private void sendTelefonnumre(DollyPerson dollyPerson, StringBuilder status) {
        if (nonNull(dollyPerson.getPdlfPerson()) && !dollyPerson.getPdlfPerson().getPerson().getTelefonnummer().isEmpty()) {

            appendResponseStatus(tpsMessagingConsumer.deleteTelefonnummerRequest(
                            dollyPerson.getHovedperson(), null),
                    status, "Telefonnummer_slett");

            appendResponseStatus(
                    tpsMessagingConsumer.sendTelefonnummerRequest(
                            dollyPerson.getHovedperson(),
                            null,
                            mapperFacade.mapAsList(dollyPerson.getPdlfPerson().getPerson().getTelefonnummer(), TelefonTypeNummerDTO.class)),
                    status,
                    "Telefonnummer_opprett"
            );
        }
    }

    private void sendSikkerhetstiltak(DollyPerson dollyPerson, StringBuilder status) {
        if (nonNull(dollyPerson.getPdlfPerson()) && !dollyPerson.getPdlfPerson().getPerson().getSikkerhetstiltak().isEmpty()) {

            appendResponseStatus(tpsMessagingConsumer.deleteSikkerhetstiltakRequest(
                            dollyPerson.getHovedperson(), null),
                    status,
                    "Sikkerhetstiltak_slett");

            var sikkerhetstiltak = dollyPerson.getPdlfPerson().getPerson().getSikkerhetstiltak()
                    .stream().findFirst();

            if (sikkerhetstiltak.isPresent()) {
                appendResponseStatus(
                        tpsMessagingConsumer.sendSikkerhetstiltakRequest(
                                dollyPerson.getHovedperson(),
                                null,
                                sikkerhetstiltak.get()),
                        status,
                        "Sikkerhetstiltak_opprett"
                );
            }
        }
    }

    private void sendSpraakkode(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, StringBuilder status) {
        if (nonNull(bestilling.getTpsMessaging()) && nonNull(bestilling.getTpsMessaging().getSpraakKode())) {
            appendResponseStatus(
                    tpsMessagingConsumer.sendSpraakkodeRequest(
                            dollyPerson.getHovedperson(),
                            null,
                            mapperFacade.map(bestilling.getTpsMessaging().getSpraakKode(), SpraakDTO.class)),
                    status,
                    "SprakKode_opprett"
            );
        }
    }

    private void sendEgenansatt(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, StringBuilder status) {

        if (nonNull(bestilling.getSkjerming()) && nonNull(bestilling.getSkjerming().getEgenAnsattDatoFom())) {
            appendResponseStatus(
                    tpsMessagingConsumer.sendEgenansattRequest(
                            dollyPerson.getHovedperson(),
                            null,
                            bestilling.getSkjerming().getEgenAnsattDatoFom().toLocalDate()),
                    status,
                    "Egenansatt_opprett"
            );
        }
        if (nonNull(bestilling.getSkjerming()) && nonNull(bestilling.getSkjerming().getEgenAnsattDatoTom()) &&
                !bestilling.getSkjerming().getEgenAnsattDatoTom().isAfter(LocalDateTime.now())) {

            appendResponseStatus(
                    tpsMessagingConsumer.deleteEgenansattRequest(
                            dollyPerson.getHovedperson(),
                            null),
                    status,
                    "Egenansatt_slett"
            );
        }
    }

    private void sendBankkontoer(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, StringBuilder status) {
        if (nonNull(bestilling.getTpsMessaging()) && nonNull(bestilling.getTpsMessaging().getUtenlandskBankkonto())) {

            appendResponseStatus(tpsMessagingConsumer.sendUtenlandskBankkontoRequest(
                            dollyPerson.getHovedperson(),
                            null,
                            bestilling.getTpsMessaging().getUtenlandskBankkonto()),
                    status, "UtenlandskBankkonto");
        }

        if (nonNull(bestilling.getTpsMessaging()) && nonNull(bestilling.getTpsMessaging().getNorskBankkonto())) {

            appendResponseStatus(tpsMessagingConsumer.sendNorskBankkontoRequest(
                            dollyPerson.getHovedperson(),
                            null,
                            bestilling.getTpsMessaging().getNorskBankkonto()),
                    status, "NorskBankkonto");
        }
    }
}
