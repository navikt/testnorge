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
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.skattekort.domain.Tilleggsopplysning.KILDESKATT_PAA_PENSJON;
import static no.nav.dolly.bestilling.skattekort.domain.Tilleggsopplysning.OPPHOLD_I_TILTAKSSONE;
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

        return oppdaterStatus(progress, getInfoVenter(SKATTEKORT.name()))
                .flatMap(updatedProgress -> Flux.fromIterable(bestilling.getSkattekort().getArbeidsgiverSkatt())
                        .flatMap(arbeidsgiver -> sendSkattekortForArbeidstaker(arbeidsgiver, dollyPerson))
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

        Flux.fromIterable(identer)
                .flatMap(ident -> skattekortConsumer.hentSkattekort(SkattekortHentRequest.builder().fnr(ident).build())
                        .flatMapMany(skattekort -> Flux.fromIterable(skattekort.getSkattekort()))
                        .flatMap(skattekort -> {
                            val context = MappingContextUtils.getMappingContext();
                            context.setProperty("ident", ident);

                            val request = mapperFacade.map(skattekort, SkattekortRequest.class, context);
                            return skattekortConsumer.sendSkattekort(request);
                        }))
                .collectList()
                .subscribe(skattekorts -> log.info("Slettet skattekort for identer: {}", identer));
    }

    private Mono<String> sendSkattekortForArbeidstaker(ArbeidstakerSkatt arbeidstaker, DollyPerson dollyPerson) {

        val context = MappingContextUtils.getMappingContext();
        context.setProperty("ident", dollyPerson.getIdent());

        return Flux.fromIterable(arbeidstaker.getArbeidstaker())
                .map(skattekortmelding ->
                        mapperFacade.map(skattekortmelding, SkattekortRequest.class, context))
                .flatMap(request -> {

                    if (request.getSkattekort().getForskuddstrekkList().stream()
                            .allMatch(forskuddstrekk -> isNull(forskuddstrekk.getTrekktabell()) &&
                                    isNull(forskuddstrekk.getProsentkort()) &&
                                    isNull(forskuddstrekk.getFrikort())) &&
                            request.getSkattekort().getTilleggsopplysningList().isEmpty()) {
                        log.warn("Utelater skattekort for person: {}, year: {} -- ingen forskuddstrekk " +
                                        "eller tillegssopplysninger er definert",
                                dollyPerson.getIdent(), request.getSkattekort().getInntektsaar());
                        return Mono.just("%d|Ingen forskuddstrekk er definert".formatted(request.getSkattekort().getInntektsaar()));
                    } else {

                        return skattekortConsumer.sendSkattekort(request)
                                .map(response -> formatStatus(response, request.getSkattekort().getInntektsaar(),
                                        dollyPerson.getIdent()));
                    }
                })
                .onErrorResume(throwable -> Mono.just("xxxx|%s".formatted(throwable.getMessage())))
                .collect(Collectors.joining(","));
    }

    private static boolean isValidateOK(RsDollyUtvidetBestilling bestilling) {

        return bestilling.getSkattekort().getArbeidsgiverSkatt().stream()
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
                                        KILDESKATT_PAA_PENSJON.equals(tilleggsopplysning) ||
                                        OPPHOLD_I_TILTAKSSONE.equals(tilleggsopplysning));
    }

    private static boolean isGyldigTrekkode(Trekkode trekkode) {

        return LOENN_FRA_NAV.equals(trekkode) ||
                PENSJON_FRA_NAV.equals(trekkode) ||
                UFOERETRYGD_FRA_NAV.equals(trekkode);
    }

    private String formatStatus(SkattekortResponse response, Integer year, String ident) {

        val prefix = year + "|";
        if (response.getStatus().is2xxSuccessful()) {
            return prefix + "Skattekort lagret";
        } else {
            log.error("Feil ved innsending av skattekort for person: {}, inntektsaar: {}: {}",
                    ident, year, response.getFeilmelding());
            return prefix + errorStatusDecoder.getStatusMessage(response.getFeilmelding());
        }
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {
        return transactionHelperService.persister(progress, BestillingProgress::getSkattekortStatus,
                BestillingProgress::setSkattekortStatus, status);
    }
}