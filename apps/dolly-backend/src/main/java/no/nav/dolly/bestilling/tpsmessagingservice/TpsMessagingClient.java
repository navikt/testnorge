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
import no.nav.testnav.libs.dto.pdlforvalter.v1.SikkerhetstiltakDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.AdresseUtlandDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.SpraakDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TelefonTypeNummerDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.concurrent.CompletableFuture.supplyAsync;

@Slf4j
@Service
@Order(6)
@RequiredArgsConstructor
public class TpsMessagingClient implements ClientRegister {

    private final TpsMessagingConsumer tpsMessagingConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final PdlDataConsumer pdlDataConsumer;
    private final ExecutorService dollyForkJoinPool;

    private static String getResponse(String melding, List<TpsMeldingResponseDTO> responseList) {

        StringBuilder respons = new StringBuilder();

        if (!responseList.isEmpty()) {
            respons.append('$')
                    .append(melding)
                    .append('#');

            responseList.forEach(response -> {
                respons.append(response.getMiljoe());
                respons.append(':');
                respons.append("OK".equals(response.getStatus()) ? "OK" : "FEIL= " + response.getUtfyllendeMelding());
                respons.append(',');
            });
        }
        return respons.toString();
    }

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        var status = new StringBuilder();

        try {
            if (isNull(dollyPerson.getPdlfPerson())) {

                dollyPerson.setPdlfPerson(pdlDataConsumer.getPersoner(List.of(dollyPerson.getHovedperson()))
                        .stream().findFirst().orElse(null));
            }

            List.of(
                            supplyAsync(() -> sendSpraakkode(bestilling, dollyPerson), dollyForkJoinPool),
                            supplyAsync(() -> sendBankkontoer(bestilling, dollyPerson), dollyForkJoinPool),
                            supplyAsync(() -> sendEgenansatt(bestilling, dollyPerson), dollyForkJoinPool),
                            supplyAsync(() -> sendSikkerhetstiltak(dollyPerson), dollyForkJoinPool),
                            supplyAsync(() -> sendTelefonnumre(dollyPerson), dollyForkJoinPool),
                            supplyAsync(() -> sendBostedsadresseUtland(dollyPerson), dollyForkJoinPool),
                            supplyAsync(() -> sendKontaktadresseUtland(dollyPerson), dollyForkJoinPool))

                    .forEach(future -> {
                        try {
                            future.get(1, TimeUnit.MINUTES)
                                    .entrySet()
                                    .forEach(entry -> status.append(getResponse(entry.getKey(), entry.getValue())));

                        } catch (InterruptedException | ExecutionException | TimeoutException e) {

                            log.error(e.getMessage(), e);
                            Thread.interrupted();
                        }
                    });

        } catch (RuntimeException e) {
            progress.setFeil(errorStatusDecoder.decodeRuntimeException(e));
            log.error("Kall til TPS messaging service feilet: {}", e.getMessage());
        }
        progress.setTpsMessagingStatus(status.toString());
    }

    @Override
    public void release(List<String> identer) {

        // TpsMessaging har ikke sletting
    }

    private Map<String, List<TpsMeldingResponseDTO>> sendBostedsadresseUtland(DollyPerson dollyPerson) {

        return nonNull(dollyPerson.getPdlfPerson()) ?

                Map.of("BostedadresseUtland",
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
                                .findFirst()
                                .orElse(emptyList()))
                : emptyMap();
    }

    private Map<String, List<TpsMeldingResponseDTO>> sendKontaktadresseUtland(DollyPerson dollyPerson) {

        return nonNull(dollyPerson.getPdlfPerson()) ?

                Map.of("KontaktadresseUtland",
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
                                .findFirst()
                                .orElse(emptyList()))
                : emptyMap();
    }

    private Map<String, List<TpsMeldingResponseDTO>> sendTelefonnumre(DollyPerson dollyPerson) {

        return nonNull(dollyPerson.getPdlfPerson()) && !dollyPerson.getPdlfPerson().getPerson().getTelefonnummer().isEmpty() ?

                Map.of("Telefonnummer_slett", tpsMessagingConsumer.deleteTelefonnummerRequest(
                                dollyPerson.getHovedperson(), null),
                        "Telefonnummer_opprett", tpsMessagingConsumer.sendTelefonnummerRequest(
                                dollyPerson.getHovedperson(),
                                null,
                                mapperFacade.mapAsList(dollyPerson.getPdlfPerson().getPerson().getTelefonnummer(),
                                        TelefonTypeNummerDTO.class)))
                : emptyMap();
    }

    private Map<String, List<TpsMeldingResponseDTO>> sendSikkerhetstiltak(DollyPerson dollyPerson) {

        return nonNull(dollyPerson.getPdlfPerson()) && !dollyPerson.getPdlfPerson().getPerson().getSikkerhetstiltak().isEmpty() ?

                Map.of("Sikkerhetstiltak_slett", tpsMessagingConsumer.deleteSikkerhetstiltakRequest(
                                dollyPerson.getHovedperson(), null),
                        "Sikkerhetstiltak_opprett", tpsMessagingConsumer.sendSikkerhetstiltakRequest(
                                dollyPerson.getHovedperson(),
                                null,
                                dollyPerson.getPdlfPerson().getPerson().getSikkerhetstiltak()
                                        .stream().findFirst().orElse(new SikkerhetstiltakDTO())))
                : emptyMap();
    }

    private Map<String, List<TpsMeldingResponseDTO>> sendSpraakkode(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson) {

        return nonNull(bestilling.getTpsMessaging()) && nonNull(bestilling.getTpsMessaging().getSpraakKode()) ?

                Map.of("SpråkKode", tpsMessagingConsumer.sendSpraakkodeRequest(
                        dollyPerson.getHovedperson(),
                        null,
                        mapperFacade.map(bestilling.getTpsMessaging().getSpraakKode(), SpraakDTO.class)))

                : emptyMap();
    }

    private Map<String, List<TpsMeldingResponseDTO>> sendEgenansatt(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson) {

        var egansatt = new HashMap<String, List<TpsMeldingResponseDTO>>();

        if (nonNull(bestilling.getSkjerming()) && nonNull(bestilling.getSkjerming().getEgenAnsattDatoTom())) {

            egansatt.put("Egenansatt_slett", tpsMessagingConsumer.deleteEgenansattRequest(
                    dollyPerson.getHovedperson(),
                    null));

        } else if (nonNull(bestilling.getSkjerming()) && nonNull(bestilling.getSkjerming().getEgenAnsattDatoFom())) {

            egansatt.put("Egenansatt_opprett", tpsMessagingConsumer.sendEgenansattRequest(
                    dollyPerson.getHovedperson(),
                    null,
                    bestilling.getSkjerming().getEgenAnsattDatoFom().toLocalDate()));
        }

        return egansatt;
    }

    private Map<String, List<TpsMeldingResponseDTO>> sendBankkontoer(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson) {

        var bankkontoer = new HashMap<String, List<TpsMeldingResponseDTO>>();

        if (nonNull(bestilling.getTpsMessaging()) && nonNull(bestilling.getTpsMessaging().getUtenlandskBankkonto())) {

            bankkontoer.put("UtenlandskBankkonto", tpsMessagingConsumer.sendUtenlandskBankkontoRequest(
                    dollyPerson.getHovedperson(),
                    null,
                    bestilling.getTpsMessaging().getUtenlandskBankkonto()));

        } else if (nonNull(bestilling.getTpsMessaging()) && nonNull(bestilling.getTpsMessaging().getNorskBankkonto())) {

            bankkontoer.put("NorskBankkonto", tpsMessagingConsumer.sendNorskBankkontoRequest(
                    dollyPerson.getHovedperson(),
                    null,
                    bestilling.getTpsMessaging().getNorskBankkonto()));
        }

        return bankkontoer;
    }
}
