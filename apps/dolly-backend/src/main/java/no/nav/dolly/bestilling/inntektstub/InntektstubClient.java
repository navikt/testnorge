package no.nav.dolly.bestilling.inntektstub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.dolly.bestilling.inntektstub.domain.InntektsinformasjonWrapper;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.service.TransactionHelperService;
import no.nav.dolly.service.TransaksjonMappingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.INNTK;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static no.nav.dolly.util.TestnorgeIdentUtility.isTestnorgeIdent;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.truncate;

@Slf4j
@Service
@RequiredArgsConstructor
public class InntektstubClient implements ClientRegister {

    private static final int MAX_STATUS_LEN = 2048;
    private static final String IMPORTERT_FRA_TENOR = "{\"status\": \"Import fra Tenor utført\"}";

    private final InntektstubConsumer inntektstubConsumer;
    private final MapperFacade mapperFacade;
    private final TransactionHelperService transactionHelperService;
    private final TransaksjonMappingService transaksjonMappingService;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        return importFraTenor(bestilling, dollyPerson, progress)
                .flatMap(status -> {
                    if (nonNull(bestilling.getInntektstub()) && !bestilling.getInntektstub().getInntektsinformasjon().isEmpty()) {

                        var context = MappingContextUtils.getMappingContext();
                        context.setProperty("ident", dollyPerson.getIdent());

                        var inntektsinformasjonWrapper = mapperFacade.map(bestilling.getInntektstub(),
                                InntektsinformasjonWrapper.class, context);

                        return inntektstubConsumer.getInntekter(dollyPerson.getIdent())
                                .collectList()
                                .flatMap(eksisterende ->
                                        Flux.fromIterable(inntektsinformasjonWrapper.getInntektsinformasjon())
                                                .filter(nyinntekt -> eksisterende.stream().noneMatch(entry ->
                                                        entry.getAarMaaned().equals(nyinntekt.getAarMaaned()) &&
                                                                entry.getVirksomhet().equals(nyinntekt.getVirksomhet()) &&
                                                                entry.getInntektsliste().stream().anyMatch(gammelt -> nyinntekt.getInntektsliste().contains(gammelt))))
                                                .collectList()
                                                .flatMapMany(inntektstubConsumer::postInntekter)
                                                .collectList()
                                                .map(inntekter -> {
                                                    log.info("Inntektstub respons {}", inntekter);
                                                    return inntekter.stream()
                                                            .map(Inntektsinformasjon::getFeilmelding)
                                                            .noneMatch(StringUtils::isNotBlank) ? "OK" :
                                                            "Feil= " + inntekter.stream()
                                                                    .map(Inntektsinformasjon::getFeilmelding)
                                                                    .filter(StringUtils::isNotBlank)
                                                                    .map(ErrorStatusDecoder::encodeStatus)
                                                                    .collect(Collectors.joining(","));
                                                }));
                    } else {
                        return Mono.just(status);
                    }
                })
                .flatMap(status -> isNotBlank(status) ? oppdaterStatus(progress, status) : Mono.empty());
    }

    private Mono<String> importFraTenor(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress) {

        return transaksjonMappingService.existAlready(INNTK, dollyPerson.getIdent())
                .flatMap(exist -> {

                    if ((isFalse(exist) && isTestnorgeIdent(dollyPerson.getIdent())) ||
                            (nonNull(bestilling.getInntektstub()) &&
                                    !bestilling.getInntektstub().getInntektsinformasjon().isEmpty())) {
                        return oppdaterStatus(progress, getInfoVenter(INNTK.getBeskrivelse()))
                                .thenReturn(exist);
                    } else {
                        return Mono.just(exist);
                    }
                })
                .flatMap(exist -> {

                    if (isFalse(exist) && isTestnorgeIdent(dollyPerson.getIdent())) {

                        return inntektstubConsumer.importInntekt(dollyPerson.getIdent())
                                .doOnNext(response -> log.info("Import fra Tenor for {} returnerte {} {}",
                                        dollyPerson.getIdent(), response.getStatus(), response.getMessage()))
                                .flatMap(importResponse -> importResponse.getStatus().is2xxSuccessful() ?
                                        transaksjonMappingService.save(TransaksjonMapping.builder()
                                                        .ident(dollyPerson.getIdent())
                                                        .system(INNTK.name())
                                                        .transaksjonId(IMPORTERT_FRA_TENOR)
                                                        .bestillingId(progress.getBestillingId())
                                                        .datoEndret(now())
                                                        .build())
                                                .then(Mono.just("OK")) :
                                        Mono.just("Feil= " + ErrorStatusDecoder.encodeStatus(
                                                "Import av inntektsdata feilet: " + importResponse.getMessage())));
                    } else {
                        return Mono.just("");
                    }
                });
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {

        return transactionHelperService.persister(progress, BestillingProgress::setInntektstubStatus,
                truncate(status, MAX_STATUS_LEN));
    }

    @Override
    public void release(List<String> identer) {

        inntektstubConsumer.deleteInntekter(identer)
                .subscribe(response -> log.info("Slettet identer fra Inntektstub"));
    }
}
