package no.nav.dolly.bestilling.tpsmessagingservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.skjermingsregister.SkjermingUtil;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.DollyPersonCache;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SikkerhetstiltakDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.AdresseUtlandDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.SpraakDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TelefonTypeNummerDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static java.util.concurrent.CompletableFuture.supplyAsync;

@Slf4j
@Service
@Order(3)
@RequiredArgsConstructor
public class TpsMessagingClient implements ClientRegister {

    private final TpsMessagingConsumer tpsMessagingConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final ExecutorService dollyForkJoinPool;
    private final DollyPersonCache dollyPersonCache;

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

        var startTime = System.currentTimeMillis();

        var status = new StringBuilder();

        try {
            dollyPersonCache.fetchIfEmpty(dollyPerson);

            var completableFuture = Stream.of(
                            sendSpraakkode(bestilling),
                            sendBankkontonummer(bestilling),
                            sendEgenansatt(bestilling),
                            sendTelefonnumreSlett(dollyPerson),
                            sendTelefonnumreOpprett(dollyPerson),
                            sendSikkerhetstiltakSlett(dollyPerson),
                            sendSikkerhetstiltakOpprett(dollyPerson),
                            sendBostedsadresseUtland(dollyPerson),
                            sendBostedsadresseUtlandPartner(dollyPerson),
                            sendKontaktadresseUtland(dollyPerson)
                    )
                    .filter(Objects::nonNull)
                    .map(completable -> supplyAsync(() -> completable.apply(bestilling, dollyPerson), dollyForkJoinPool))
                    .toList();

            completableFuture
                    .forEach(future -> {
                        try {
                            future.get(15, TimeUnit.SECONDS)
                                    .forEach((key, value) -> status.append(getResponse(key, value)));
                        } catch (InterruptedException e) {
                            log.error(e.getMessage(), e);
                            Thread.currentThread().interrupt();
                        } catch (ExecutionException e) {
                            log.error(e.getMessage(), e);
                            Thread.interrupted();
                        } catch (TimeoutException e) {
                            log.warn("Tidsavbrudd (15 s) ved sending til TPS");
                            Thread.interrupted();
                        }
                    });

        } catch (RuntimeException e) {
            progress.setFeil(errorStatusDecoder.decodeThrowable(e));
            log.error("Kall til TPS messaging service feilet: {}", e.getMessage());
        }
        progress.setTpsMessagingStatus(status.toString());

        log.info("TpsMessaging for ident {} tok {} ms", dollyPerson.getHovedperson(), System.currentTimeMillis() - startTime);
    }

    @Override
    public void release(List<String> identer) {

        // TpsMessaging har ikke sletting
    }

    private TpsMessage sendBostedsadresseUtland(DollyPerson dollyPerson) {

        return nonNull(dollyPerson.getPdlfPerson()) && dollyPerson.getPdlfPerson().getPerson().getBostedsadresse().stream()
                .anyMatch(BostedadresseDTO::isAdresseUtland) ?

                (bestilling, dollyPerson1) ->
                        Map.of("BostedadresseUtland",
                                tpsMessagingConsumer.sendAdresseUtlandRequest(dollyPerson1.getHovedperson(), null,
                                        mapperFacade.map(dollyPerson1.getPdlfPerson().getPerson().getBostedsadresse().stream()
                                                .filter(AdresseDTO::isAdresseUtland)
                                                .findFirst().orElse(new BostedadresseDTO()), AdresseUtlandDTO.class)))

                : null;
    }

    private TpsMessage sendBostedsadresseUtlandPartner(DollyPerson dollyPerson) {

        return nonNull(dollyPerson.getPdlfPerson()) && dollyPerson.getPdlfPerson().getRelasjoner().stream()
                .filter(relasjon -> relasjon.getRelasjonType() == RelasjonType.EKTEFELLE_PARTNER)
                .map(FullPersonDTO.RelasjonDTO::getRelatertPerson)
                .map(PersonDTO::getBostedsadresse)
                .flatMap(Collection::stream)
                .anyMatch(AdresseDTO::isAdresseUtland) ?

                (bestilling, dollyPerson1) ->
                        Map.of("BostedadresseUtlandPartner",
                                tpsMessagingConsumer.sendAdresseUtlandRequest(dollyPerson.getPdlfPerson().getRelasjoner().stream()
                                                .filter(relasjon -> relasjon.getRelasjonType() == RelasjonType.EKTEFELLE_PARTNER)
                                                .map(FullPersonDTO.RelasjonDTO::getRelatertPerson)
                                                .map(PersonDTO::getIdent)
                                                .findFirst().orElse("00000000000"), null,
                                        mapperFacade.map(dollyPerson.getPdlfPerson().getRelasjoner().stream()
                                                .filter(relasjon -> relasjon.getRelasjonType() == RelasjonType.EKTEFELLE_PARTNER)
                                                .map(FullPersonDTO.RelasjonDTO::getRelatertPerson)
                                                .map(PersonDTO::getBostedsadresse)
                                                .flatMap(Collection::stream)
                                                .filter(AdresseDTO::isAdresseUtland)
                                                .findFirst().orElse(new BostedadresseDTO()), AdresseUtlandDTO.class)))

                : null;
    }

    private TpsMessage sendKontaktadresseUtland(DollyPerson dollyPerson) {

        return nonNull(dollyPerson.getPdlfPerson()) && dollyPerson.getPdlfPerson().getPerson().getKontaktadresse().stream()
                .anyMatch(KontaktadresseDTO::isAdresseUtland) ?

                (bestilling, dollyPerson1) ->
                        Map.of("KontaktadresseUtland",
                                tpsMessagingConsumer.sendAdresseUtlandRequest(dollyPerson1.getHovedperson(), null,
                                        mapperFacade.map(dollyPerson1.getPdlfPerson().getPerson().getKontaktadresse().stream()
                                                .filter(AdresseDTO::isAdresseUtland)
                                                .findFirst().orElse(new KontaktadresseDTO()), AdresseUtlandDTO.class)))
                : null;
    }

    private TpsMessage sendTelefonnumreSlett(DollyPerson dollyPerson) {

        return nonNull(dollyPerson.getPdlfPerson()) && !dollyPerson.getPdlfPerson().getPerson().getTelefonnummer().isEmpty() ?

                (bestilling, dollyPerson1) ->
                        Map.of("Telefonnummer_slett",
                                tpsMessagingConsumer.deleteTelefonnummerRequest(
                                        dollyPerson1.getHovedperson(), null))

                : null;
    }

    private TpsMessage sendTelefonnumreOpprett(DollyPerson dollyPerson) {

        return nonNull(dollyPerson.getPdlfPerson()) && !dollyPerson.getPdlfPerson().getPerson().getTelefonnummer().isEmpty() ?

                (bestilling, dollyPerson1) ->
                        Map.of("Telefonnummer_opprett",
                                tpsMessagingConsumer.sendTelefonnummerRequest(
                                        dollyPerson1.getHovedperson(),
                                        null,
                                        mapperFacade.mapAsList(dollyPerson1.getPdlfPerson().getPerson().getTelefonnummer(),
                                                TelefonTypeNummerDTO.class)))

                : null;
    }

    private TpsMessage sendSikkerhetstiltakSlett(DollyPerson dollyPerson) {

        return nonNull(dollyPerson.getPdlfPerson()) && !dollyPerson.getPdlfPerson().getPerson().getSikkerhetstiltak().isEmpty() ?

                (bestilling, dollyPerson1) ->
                        Map.of("Sikkerhetstiltak_slett",
                                tpsMessagingConsumer.deleteSikkerhetstiltakRequest(
                                        dollyPerson1.getHovedperson(), null))

                : null;
    }

    private TpsMessage sendSikkerhetstiltakOpprett(DollyPerson dollyPerson) {

        return nonNull(dollyPerson.getPdlfPerson()) && !dollyPerson.getPdlfPerson().getPerson().getSikkerhetstiltak().isEmpty() ?

                (bestilling, dollyPerson1) ->
                        Map.of("Sikkerhetstiltak_opprett",
                                tpsMessagingConsumer.sendSikkerhetstiltakRequest(
                                        dollyPerson1.getHovedperson(),
                                        null,
                                        dollyPerson1.getPdlfPerson().getPerson().getSikkerhetstiltak()
                                                .stream().findFirst().orElse(new SikkerhetstiltakDTO())))

                : null;
    }

    private TpsMessage sendSpraakkode(RsDollyUtvidetBestilling bestilling) {

        return nonNull(bestilling.getTpsMessaging()) && nonNull(bestilling.getTpsMessaging().getSpraakKode()) ?

                (bestilling1, dollyPerson) ->
                        Map.of("SpråkKode",
                                tpsMessagingConsumer.sendSpraakkodeRequest(
                                        dollyPerson.getHovedperson(),
                                        null,
                                        mapperFacade.map(bestilling1.getTpsMessaging().getSpraakKode(), SpraakDTO.class)))

                : null;
    }

    private TpsMessage sendEgenansatt(RsDollyUtvidetBestilling bestilling) {

        if (nonNull(SkjermingUtil.getEgenansattDatoTom(bestilling))) {

            return (bestilling1, dollyPerson) ->
                    Map.of("Egenansatt_slett",
                            tpsMessagingConsumer.deleteEgenansattRequest(
                                    dollyPerson.getHovedperson(),
                                    null));

        } else if (nonNull(SkjermingUtil.getEgenansattDatoFom(bestilling))) {

            return (bestilling1, dollyPerson) ->
                    Map.of("Egenansatt_opprett",
                            tpsMessagingConsumer.sendEgenansattRequest(
                                    dollyPerson.getHovedperson(),
                                    null,
                                    SkjermingUtil.getEgenansattDatoFom(bestilling).toLocalDate()));

        } else {

            return null;
        }
    }

    private TpsMessage sendBankkontonummer(RsDollyUtvidetBestilling bestilling) {
        if (nonNull(bestilling.getBankkonto())) {
            if (nonNull(bestilling.getBankkonto().getUtenlandskBankkonto())) {

                return (bestilling1, dollyPerson) ->
                        Map.of("UtenlandskBankkonto",
                                tpsMessagingConsumer.sendUtenlandskBankkontoRequest(
                                        dollyPerson.getHovedperson(),
                                        null,
                                        bestilling1.getBankkonto().getUtenlandskBankkonto()));

            } else if (nonNull(bestilling.getBankkonto().getNorskBankkonto())) {

                return (bestilling1, dollyPerson) ->
                        Map.of("NorskBankkonto",
                                tpsMessagingConsumer.sendNorskBankkontoRequest(
                                        dollyPerson.getHovedperson(),
                                        null,
                                        bestilling1.getBankkonto().getNorskBankkonto()));

            }
        }

        // den er for bakoverkompatibel
        if (nonNull(bestilling.getTpsMessaging()) && nonNull(bestilling.getTpsMessaging().getUtenlandskBankkonto())) {

            return (bestilling1, dollyPerson) ->
                    Map.of("UtenlandskBankkonto",
                            tpsMessagingConsumer.sendUtenlandskBankkontoRequest(
                                    dollyPerson.getHovedperson(),
                                    null,
                                    bestilling1.getTpsMessaging().getUtenlandskBankkonto()));

        } else if (nonNull(bestilling.getTpsMessaging()) && nonNull(bestilling.getTpsMessaging().getNorskBankkonto())) {

            return (bestilling1, dollyPerson) ->
                    Map.of("NorskBankkonto",
                            tpsMessagingConsumer.sendNorskBankkontoRequest(
                                    dollyPerson.getHovedperson(),
                                    null,
                                    bestilling1.getTpsMessaging().getNorskBankkonto()));
        } else {

            return null;
        }
    }

    public Object status() {
        return tpsMessagingConsumer.checkStatus();
    }
}
