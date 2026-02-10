package no.nav.dolly.bestilling.skattekort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.skattekort.domain.ArbeidstakerSkatt;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortHentRequest;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortRequest;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.service.TransactionHelperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkattekortClient implements ClientRegister {

    private static final String SYSTEM = "SKATTEKORT";

    private final SkattekortConsumer skattekortConsumer;
    private final MapperFacade mapperFacade;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson,
                                                BestillingProgress progress, boolean isOpprettEndre) {

        if (isNull(bestilling.getSkattekort()) || bestilling.getSkattekort().getArbeidsgiverSkatt().isEmpty()) {
            return Mono.empty();
        }

        return oppdaterStatus(progress, getInfoVenter(SYSTEM))
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

        MappingContext context = MappingContextUtils.getMappingContext();
        context.setProperty("ident", dollyPerson.getIdent());

        return Flux.fromIterable(arbeidstaker.getArbeidstaker())
                .map(skattekortmelding ->
                    mapperFacade.map(skattekortmelding, SkattekortRequest.class, context)
                )
                .flatMap(request -> {

                    if (request.getSkattekort().getForskuddstrekkList().stream()
                            .anyMatch(forskuddstrekk -> isNull(forskuddstrekk.getTrekktabell()) &&
                                            isNull(forskuddstrekk.getProsentkort()) &&
                                            isNull(forskuddstrekk.getFrikort()))) {
                        log.warn("Utelater skattekort for person: {}, year: {} -- ingen forskuddstrekk er definert",
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

    private String formatStatus(SkattekortResponse response, Integer year, String ident) {

        val prefix = year + "|";
        if (response.getStatus().is2xxSuccessful()) {
            return prefix + "Skattekort lagret";
        } else {
            log.error("Feil ved innsending av skattekort for person: {}, inntektsaar: {}: {}",
                    ident, year, response.getFeilmelding());
            return prefix + response.getFeilmelding();
        }
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {
        return transactionHelperService.persister(progress, BestillingProgress::getSkattekortStatus,
                BestillingProgress::setSkattekortStatus, status);
    }
}
