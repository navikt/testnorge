package no.nav.dolly.bestilling.arenaforvalter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaDagpenger;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeDagpengerResponse;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getVarselSlutt;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArenaForvalterClient implements ClientRegister {

    private static final String STATUS_FMT = "%s$%s";
    private static final String SYSTEM = "Arena";

    private final ArenaForvalterConsumer arenaForvalterConsumer;
    private final MapperFacade mapperFacade;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final PersonServiceConsumer personServiceConsumer;
    private final TransactionHelperService transactionHelperService;
    private final PdlPersonConsumer pdlPersonConsumer;

    private static ArenaNyeBrukere filtrerEksisterendeBrukere(ArenaNyeBrukere arenaNyeBrukere) {

        return new ArenaNyeBrukere(arenaNyeBrukere.getNyeBrukere().stream()
                .filter(arenaNyBruker ->
                        (nonNull(arenaNyBruker.getKvalifiseringsgruppe()) || nonNull(arenaNyBruker.getUtenServicebehov())))
                .toList());
    }

    private static String getFeilbeskrivelse(String feil) {

        log.error("Arena error {}", feil);
        return !feil.contains("User Defined Resource Error") ?
                ErrorStatusDecoder.encodeStatus(feil) : "";
    }

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getArenaforvalter())) {

            return Flux.from(getIdenterFamilie(dollyPerson.getIdent())
                    .flatMap(personServiceConsumer::getPdlSyncReady)
                    .collectList()
                    .map(status -> status.stream().allMatch(BooleanUtils::isTrue))
                    .map(isPdlFamilyReady -> doArenaOpprett(isPdlFamilyReady, bestilling, dollyPerson))
                    .flatMap(Mono::from)
                    .map(status -> futurePersist(progress, status)));
        }
        return Flux.empty();
    }

    private Mono<String> doFeilmelding(RsDollyUtvidetBestilling bestilling) {
        return Mono.just(bestilling.getEnvironments().stream()
                .map(miljo -> String.format(STATUS_FMT, miljo, encodeStatus(getVarselSlutt(SYSTEM))))
                .collect(Collectors.joining(",")));
    }

    private Mono<String> doArenaOpprett(Boolean isPdlReady, RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson) {

        return isTrue(isPdlReady) ?
                arenaForvalterConsumer.getToken()
                        .flatMapMany(token -> arenaForvalterConsumer.getEnvironments(token)
                                .filter(miljo -> bestilling.getEnvironments().contains(miljo))
                                .parallel()
                                .flatMap(miljo -> Flux.concat(arenaForvalterConsumer.deleteIdent(dollyPerson.getIdent(), miljo, token),
                                        sendArenadata(bestilling.getArenaforvalter(), dollyPerson.getIdent(), miljo, token),
                                        sendArenadagpenger(bestilling.getArenaforvalter(), dollyPerson.getIdent(), miljo, token))))
                        .filter(StringUtils::isNotBlank)
                        .collect(Collectors.joining(",")) :

                doFeilmelding(bestilling);
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            progress.setArenaforvalterStatus(status);
            transactionHelperService.persister(progress);
            return progress;
        };
    }

    @Override
    public void release(List<String> identer) {

        arenaForvalterConsumer.deleteIdenter(identer)
                .subscribe(response -> log.info("Slettet utført mot Arena-forvalteren"));
    }

    private Flux<String> getIdenterFamilie(String ident) {

        return pdlPersonConsumer.getPdlPersoner(List.of(ident))
                .filter(pdlPersonBolk -> nonNull(pdlPersonBolk.getData()))
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Flux::fromIterable)
                .filter(personBolk -> nonNull(personBolk.getPerson()))
                .map(PdlPersonBolk.PersonBolk::getPerson)
                .map(person -> Stream.of(
                                person.getSivilstand().stream()
                                        .map(PdlPerson.Sivilstand::getRelatertVedSivilstand)
                                        .filter(Objects::nonNull)
                                        .toList(),
                                person.getForelderBarnRelasjon().stream()
                                        .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                                        .filter(Objects::nonNull)
                                        .toList())
                        .flatMap(Collection::stream)
                        .distinct()
                        .toList())
                .flatMap(Flux::fromIterable);
    }

    private Flux<String> sendArenadata(Arenadata arenadata, String ident, String miljoe, AccessToken token) {

        var arenaNyeBrukere = ArenaNyeBrukere.builder()
                .nyeBrukere(List.of(mapperFacade.map(arenadata, ArenaNyBruker.class)))
                .build();
        arenaNyeBrukere.getNyeBrukere().get(0).setPersonident(ident);
        arenaNyeBrukere.getNyeBrukere().get(0).setMiljoe(miljoe);

        var filtrerteBrukere = filtrerEksisterendeBrukere(arenaNyeBrukere);
        if (filtrerteBrukere.getNyeBrukere().isEmpty()) {
            log.info("Alle brukere eksisterer i Arena allerede.");
            return Flux.empty();
        }

        return arenaForvalterConsumer.postArenadata(filtrerteBrukere, token)
                .map(respons -> {
                    log.info("Arena respons {}", respons);
                    return respons.getArbeidsokerList().stream()
                            .filter(arbeidsoker -> "OK".equals(arbeidsoker.getStatus()))
                            .filter(arbeidsoker -> arenadata.getDagpenger().isEmpty())
                            .map(arbeidsoker -> String.format(STATUS_FMT, arbeidsoker.getMiljoe(), arbeidsoker.getStatus()))
                            .collect(Collectors.joining(","))
                            +
                            respons.getNyBrukerFeilList().stream()
                                    .map(brukerfeil ->
                                            String.format("%s$Feil: %s. Se detaljer i logg. %s", brukerfeil.getMiljoe(),
                                                    brukerfeil.getNyBrukerFeilstatus(), getFeilbeskrivelse(brukerfeil.getMelding())))
                                    .collect(Collectors.joining(","));
                });
    }

    private Flux<String> sendArenadagpenger(Arenadata arenadata, String ident, String miljoe, AccessToken token) {

        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", ident);
        context.setProperty("miljoe", miljoe);

        return Flux.fromIterable(arenadata.getDagpenger())
                .map(ettSettDagpenger -> mapperFacade.map(arenadata, ArenaDagpenger.class, context))
                .flatMap(dagpenger -> arenaForvalterConsumer.postArenaDagpenger(dagpenger, token))
                .map(response -> {
                    log.info("Arena respons {}", response);
                    return response.getNyeDagpFeilList().stream()
                            .map(brukerfeil -> String.format("%s$Feil: OPPRETT_DAGPENGER %s", brukerfeil.getMiljoe(),
                                    errorStatusDecoder.getStatusMessage(brukerfeil.getMelding())))
                            .collect(Collectors.joining(",")) +

                            response.getNyeDagp().stream()
                                    .map(ArenaNyeDagpengerResponse.Dagp::getNyeDagpResponse)
                                    .filter(Objects::nonNull)
                                    .map(dagp -> String.format(STATUS_FMT, miljoe,
                                            ("JA".equals(dagp.getUtfall()) ? "OK" : ("Feil: OPPRETT_DAGPENGER " +
                                                    errorStatusDecoder.getStatusMessage(dagp.getBegrunnelse())))))
                                    .collect(Collectors.joining(",")) +

                            response.getNyeDagp().stream()
                                    .filter(oppretting -> response.getNyeDagp().isEmpty() && response.getNyeDagpFeilList().isEmpty())
                                    .map(oppretting -> String.format("%s$OK", miljoe))
                                    .collect(Collectors.joining(","));
                });
    }
}
