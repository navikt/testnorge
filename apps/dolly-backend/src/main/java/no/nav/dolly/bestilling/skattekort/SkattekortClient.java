package no.nav.dolly.bestilling.skattekort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.skattekort.domain.ArbeidstakerSkatt;
import no.nav.dolly.bestilling.skattekort.domain.Skattekort;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortHentRequest;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortRequest;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortResponse;
import no.nav.dolly.bestilling.skattekort.domain.Skattekortmelding;
import no.nav.dolly.bestilling.skattekort.domain.Trekkode;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.service.TransactionHelperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.skattekort.domain.Resultatstatus.IKKE_SKATTEKORT;
import static no.nav.dolly.bestilling.skattekort.domain.Resultatstatus.IKKE_TREKKPLIKT;
import static no.nav.dolly.bestilling.skattekort.domain.Tilleggsopplysning.KILDESKATT_PAA_PENSJON;
import static no.nav.dolly.bestilling.skattekort.domain.Tilleggsopplysning.OPPHOLD_PAA_SVALBARD;
import static no.nav.dolly.bestilling.skattekort.domain.Trekkode.LOENN_FRA_NAV;
import static no.nav.dolly.bestilling.skattekort.domain.Trekkode.PENSJON_FRA_NAV;
import static no.nav.dolly.bestilling.skattekort.domain.Trekkode.UFOERETRYGD_FRA_NAV;
import static no.nav.dolly.domain.resultset.SystemTyper.SKATTEKORT;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkattekortClient implements ClientRegister {

    public static final Set<String> MILJOER_SUPPORTED = Set.of("q1", "q2");
    private static final Set<String> DEFAULT_MILJOER = Set.of("q2");

    private final SkattekortConsumer skattekortConsumer;
    private final MapperFacade mapperFacade;
    private final TransactionHelperService transactionHelperService;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson,
                                                BestillingProgress progress, boolean isOpprettEndre) {

        if (isNull(bestilling.getSkattekort()) || bestilling.getSkattekort().getArbeidsgiverSkatt().isEmpty()) {
            return Mono.empty();
        } else if (!isValidateOK(bestilling)) {
            return oppdaterStatus(progress, "Avvik: Validering feilet: Trekkode er ikke gyldig");
        }

        var filteredMiljoer = bestilling.getEnvironments().stream()
                .filter(MILJOER_SUPPORTED::contains)
                .collect(Collectors.toSet());

        var miljoer = filteredMiljoer.isEmpty() ? DEFAULT_MILJOER : filteredMiljoer;

        return oppdaterStatus(progress, miljoer.stream()
                .map(miljo -> "%s:%s".formatted(miljo, getInfoVenter(SKATTEKORT.name())))
                .collect(Collectors.joining(",")))
                .flatMap(updatedProgress -> Flux.fromIterable(miljoer)
                        .flatMap(miljoe -> Flux.fromIterable(bestilling.getSkattekort().getArbeidsgiverSkatt())
                                .flatMap(arbeidsgiver -> sendSkattekortForArbeidstaker(arbeidsgiver, dollyPerson, miljoe))
                                .collect(Collectors.joining(",")))
                        .collect(Collectors.joining(","))
                        .flatMap(resultat -> {
                            if (isNotBlank(resultat)) {
                                return oppdaterStatus(updatedProgress, resultat);
                            } else {
                                return Mono.just(updatedProgress);
                            }
                        }));
    }

    @Override
    public void release(List<String> identer) {

        Flux.fromIterable(MILJOER_SUPPORTED)
                .flatMap(miljoe -> Flux.fromIterable(identer)
                        .flatMap(ident -> skattekortConsumer.hentSkattekort(SkattekortHentRequest.builder().fnr(ident).build(), miljoe)
                                .flatMapMany(skattekort -> Flux.fromIterable(skattekort.getSkattekort()))
                                .flatMap(skattekort -> {
                                    val context = MappingContextUtils.getMappingContext();
                                    context.setProperty("ident", ident);

                                    val request = mapperFacade.map(skattekort, SkattekortRequest.class, context);
                                    return skattekortConsumer.sendSkattekort(request, miljoe);
                                })))
                .collectList()
                .subscribe(skattekorts -> log.info("Slettet skattekort for identer: {}", identer));
    }

    private Mono<String> sendSkattekortForArbeidstaker(ArbeidstakerSkatt arbeidstaker, DollyPerson dollyPerson, String miljoe) {

        val context = MappingContextUtils.getMappingContext();
        context.setProperty("ident", dollyPerson.getIdent());

        return Flux.fromIterable(arbeidstaker.getArbeidstaker())
                .map(skattekortmelding ->
                        mapperFacade.map(skattekortmelding, SkattekortRequest.class, context))
                .flatMap(request -> skattekortConsumer.sendSkattekort(request, miljoe)
                        .map(response -> formatStatus(response, request.getSkattekort().getInntektsaar(),
                                dollyPerson.getIdent(), miljoe)))
                .onErrorResume(throwable -> Mono.just("%s:%s".formatted(miljoe, throwable.getMessage())))
                .collect(Collectors.joining(","));
    }

    private static boolean isValidateOK(RsDollyUtvidetBestilling bestilling) {

        return bestilling.getSkattekort().getArbeidsgiverSkatt().stream()
                .map(ArbeidstakerSkatt::getArbeidstaker)
                .flatMap(Collection::stream)
                .map(Skattekortmelding::getResultatPaaForespoersel)
                .anyMatch(status ->
                        IKKE_TREKKPLIKT.equals(status) ||
                                IKKE_SKATTEKORT.equals(status))
                ||
                bestilling.getSkattekort().getArbeidsgiverSkatt().stream()
                        .map(ArbeidstakerSkatt::getArbeidstaker)
                        .flatMap(Collection::stream)
                        .map(Skattekortmelding::getSkattekort)
                        .filter(Objects::nonNull)
                        .map(Skattekort::getForskuddstrekk)
                        .flatMap(Collection::stream)
                        .anyMatch(forskuddstrekk ->
                                nonNull(forskuddstrekk.getFrikort()) &&
                                        isGyldigTrekkode(forskuddstrekk.getFrikort().getTrekkode()) ||
                                        nonNull(forskuddstrekk.getTrekkprosent()) &&
                                                isGyldigTrekkode(forskuddstrekk.getTrekkprosent().getTrekkode()) ||
                                        nonNull(forskuddstrekk.getTrekktabell()) &&
                                                isGyldigTrekkode(forskuddstrekk.getTrekktabell().getTrekkode()))
                ||
                bestilling.getSkattekort().getArbeidsgiverSkatt().stream()
                        .map(ArbeidstakerSkatt::getArbeidstaker)
                        .flatMap(Collection::stream)
                        .map(Skattekortmelding::getTilleggsopplysning)
                        .flatMap(Collection::stream)
                        .anyMatch(tilleggsopplysning ->
                                OPPHOLD_PAA_SVALBARD.equals(tilleggsopplysning) ||
                                        KILDESKATT_PAA_PENSJON.equals(tilleggsopplysning));
    }

    private static boolean isGyldigTrekkode(Trekkode trekkode) {

        return LOENN_FRA_NAV.equals(trekkode) ||
                PENSJON_FRA_NAV.equals(trekkode) ||
                UFOERETRYGD_FRA_NAV.equals(trekkode);
    }

    private String formatStatus(SkattekortResponse response, Integer year, String ident, String miljoe) {

        if (response.getStatus().is2xxSuccessful()) {
            return "%s:OK".formatted(miljoe);
        } else {
            log.error("Feil ved innsending av skattekort for person: {}, miljoe: {}, inntektsaar: {}: {}",
                    ident, miljoe, year, response.getFeilmelding());
            return "%s:%s".formatted(miljoe, errorStatusDecoder.getStatusMessage(response.getFeilmelding()));
        }
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {
        return transactionHelperService.persister(progress, BestillingProgress::getSkattekortStatus,
                BestillingProgress::setSkattekortStatus, status);
    }
}