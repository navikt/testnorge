package no.nav.dolly.bestilling.tpsmessagingservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.bestilling.skjermingsregister.SkjermingUtil;
import no.nav.dolly.config.ApplicationConfig;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.kontoregisterservice.util.BankkontoGenerator.tilfeldigNorskBankkonto;
import static no.nav.dolly.bestilling.kontoregisterservice.util.BankkontoGenerator.tilfeldigUtlandskBankkonto;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class TpsMessagingClient implements ClientRegister {

    private static final String SEP = "$";
    private static final String STATUS_FMT = "%s:%s";
    private static final String TPS_MESSAGING = "TPS";

    private final TpsMessagingConsumer tpsMessagingConsumer;
    private final PersonServiceConsumer personServiceConsumer;
    private final TransactionHelperService transactionHelperService;
    private final MiljoerConsumer miljoerConsumer;
    private final ApplicationConfig applicationConfig;

    private static String getResultat(TpsMeldingResponseDTO respons) {

        return "OK".equals(respons.getStatus()) ? "OK" :
                ErrorStatusDecoder.encodeStatus("FEIL: " + respons.getUtfyllendeMelding());
    }

    private static String getStatus(String melding, List<TpsMeldingResponseDTO> statuser) {

        return !statuser.isEmpty() ?

                "%s#%s".formatted(melding,
                        statuser.stream()
                                .map(respons -> STATUS_FMT.formatted(
                                        respons.getMiljoe(),
                                        getResultat(respons)))
                                .collect(Collectors.joining(","))) :
                "";
    }

    @Override
    @SuppressWarnings("S1144")
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        return miljoerConsumer.getMiljoer()
                .flatMap(miljoer -> {

                    if (!dollyPerson.isOrdre() && isTpsMessage(bestilling)) {
                        transactionHelperService.persister(progress, BestillingProgress::getTpsMessagingStatus,
                                BestillingProgress::setTpsMessagingStatus,
                                prepTpsMessagingStatus(miljoer), SEP);
                    }

                    return getIdenterHovedpersonOgPartner(dollyPerson.getIdent())
                            .flatMap(this::getPersonData)
                            .collectList()
                            .flatMapMany(personer -> Flux.concat(
                                    sendBankkontonummerNorge(bestilling, dollyPerson.getIdent())
                                            .map(respons -> Map.of("NorskBankkonto", respons)),
                                    sendBankkontonummerUtenland(bestilling, dollyPerson.getIdent())
                                            .map(respons -> Map.of("UtenlandskBankkonto", respons)),
                                    sendEgenansattSlett(bestilling, dollyPerson.getIdent())
                                            .map(respons -> Map.of("Egenansatt_slett", respons)),
                                    sendEgenansatt(bestilling, dollyPerson.getIdent())
                                            .map(respons -> Map.of("Egenansatt_opprett", respons))
                            ))
                            .map(respons -> respons.entrySet().stream()
                                    .map(entry -> getStatus(entry.getKey(), entry.getValue()))
                                    .toList())
                            .flatMap(Flux::fromIterable)
                            .filter(StringUtils::isNotBlank)
                            .timeout(Duration.ofSeconds(applicationConfig.getClientTimeout()))
                            .onErrorResume(error -> getError(error, miljoer))
                            .collect(Collectors.joining(SEP));
                })
                .flatMap(status -> oppdaterStatus(dollyPerson, progress, status));
    }

    private Flux<String> getError(Throwable error, List<String> miljoer) {

        return Flux.just("Meldinger til TPS#%s".formatted(
                miljoer.stream()
                        .map(miljoe -> STATUS_FMT.formatted(
                                miljoe,
                                "FEIL= " + ErrorStatusDecoder.encodeStatus(WebClientError.describe(error).getMessage())))
                        .collect(Collectors.joining(","))));
    }

    private boolean isTpsMessage(RsDollyUtvidetBestilling bestilling) {

        return nonNull(bestilling.getBankkonto()) ||
                nonNull(bestilling.getSkjerming()) ||

                (nonNull(bestilling.getPdldata()) &&
                        nonNull(bestilling.getPdldata().getPerson()));
    }

    private Mono<BestillingProgress> oppdaterStatus(DollyPerson dollyPerson, BestillingProgress progress, String status) {

        if (!dollyPerson.isOrdre()) {
            return transactionHelperService.persister(progress, BestillingProgress::getTpsMessagingStatus,
                    BestillingProgress::setTpsMessagingStatus, status, SEP);
        }
        return Mono.just(progress);
    }

    private String prepTpsMessagingStatus(List<String> miljoer) {

        return "Startet#" + miljoer.stream()
                .map(miljo -> String.format(STATUS_FMT, miljo, getInfoVenter(TPS_MESSAGING)))
                .collect(Collectors.joining(","));
    }

    @Override
    public void release(List<String> identer) {

        // TpsMessaging har ikke sletting
    }

    private Flux<List<String>> getIdenterHovedpersonOgPartner(String ident) {

        return getPersonData(List.of(ident))
                .map(person -> Stream.of(List.of(ident),
                                person.getPerson().getSivilstand().stream()
                                        .map(PdlPerson.Sivilstand::getRelatertVedSivilstand)
                                        .filter(Objects::nonNull)
                                        .toList())
                        .flatMap(Collection::stream)
                        .distinct()
                        .toList());
    }

    private Flux<PdlPersonBolk.PersonBolk> getPersonData(List<String> identer) {

        return personServiceConsumer.getPdlPersoner(identer)
                .filter(pdlPersonBolk -> nonNull(pdlPersonBolk.getData()))
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Flux::fromIterable)
                .filter(personBolk -> nonNull(personBolk.getPerson()));
    }

    private Mono<List<TpsMeldingResponseDTO>> sendEgenansattSlett(RsDollyUtvidetBestilling bestilling,
                                                                  String ident) {

        return nonNull(SkjermingUtil.getEgenansattDatoTom(bestilling)) ?

                tpsMessagingConsumer.deleteEgenansattRequest(ident, null)
                        .collectList() :

                Mono.just(emptyList());
    }

    private Mono<List<TpsMeldingResponseDTO>> sendEgenansatt(RsDollyUtvidetBestilling bestilling,
                                                             String ident) {

        return nonNull(SkjermingUtil.getEgenansattDatoFom(bestilling)) ?

                tpsMessagingConsumer.sendEgenansattRequest(ident, null,
                                toLocalDate(SkjermingUtil.getEgenansattDatoFom(bestilling)))
                        .collectList() :

                Mono.just(emptyList());
    }

    @SuppressWarnings("S1144")
    private Mono<List<TpsMeldingResponseDTO>> sendBankkontonummerNorge(RsDollyUtvidetBestilling bestilling,
                                                                       String ident) {

        if (nonNull(bestilling.getBankkonto()) && nonNull(bestilling.getBankkonto().getNorskBankkonto())) {

            if (isTrue(bestilling.getBankkonto().getNorskBankkonto().getTilfeldigKontonummer())) {
                bestilling.getBankkonto().getNorskBankkonto().setKontonummer(tilfeldigNorskBankkonto());
            }

            return tpsMessagingConsumer.sendNorskBankkontoRequest(
                            ident, null, bestilling.getBankkonto().getNorskBankkonto())
                    .collectList();

        } else {
            return Mono.just(emptyList());
        }
    }

    @SuppressWarnings("S1144")
    private Mono<List<TpsMeldingResponseDTO>> sendBankkontonummerUtenland(RsDollyUtvidetBestilling bestilling,
                                                                          String ident) {

        if (nonNull(bestilling.getBankkonto()) && nonNull(bestilling.getBankkonto().getUtenlandskBankkonto())) {

            if (isTrue(bestilling.getBankkonto().getUtenlandskBankkonto().getTilfeldigKontonummer())) {
                bestilling.getBankkonto().getUtenlandskBankkonto()
                        .setKontonummer(tilfeldigUtlandskBankkonto(
                                bestilling.getBankkonto().getUtenlandskBankkonto().getLandkode()));
            }

            return tpsMessagingConsumer.sendUtenlandskBankkontoRequest(
                            ident, null, bestilling.getBankkonto().getUtenlandskBankkonto())
                    .collectList();

        } else {

            return Mono.just(emptyList());
        }
    }

    private LocalDate toLocalDate(LocalDateTime datoOgTid) {

        return nonNull(datoOgTid) ? datoOgTid.toLocalDate() : null;
    }
}
