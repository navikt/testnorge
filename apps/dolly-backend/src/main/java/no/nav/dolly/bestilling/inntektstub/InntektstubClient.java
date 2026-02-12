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
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.INNTK;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static no.nav.dolly.util.TestnorgeIdentUtility.isTestnorgeIdent;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.StringUtils.truncate;

@Slf4j
@Service
@RequiredArgsConstructor
public class InntektstubClient implements ClientRegister {

    private static final int MAX_STATUS_LEN = 2048;
    private static final String INGENDATA_FRA_TENOR = "{\"status\": \"Data fra Tenor ikke funnet\"}";
    private static final String IMPORTERT_FRA_TENOR = "{\"status\": \"Import fra Tenor utført\"}";

    private final InntektstubConsumer inntektstubConsumer;
    private final MapperFacade mapperFacade;
    private final TransactionHelperService transactionHelperService;
    private final TransaksjonMappingService transaksjonMappingService;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (!isTestnorgeIdent(dollyPerson.getIdent()) &&
                (isNull(bestilling.getInntektstub()) || bestilling.getInntektstub().getInntektsinformasjon().isEmpty())) {
            return Mono.empty();
        }

        return Mono.just(bestilling)
                .flatMap(best -> {
                    if (isTestnorgeIdent(dollyPerson.getIdent())) {
                        return importFraTenor(dollyPerson, progress);
                    } else {
                        return nonNull(bestilling.getInntektstub()) && !bestilling.getInntektstub().getInntektsinformasjon().isEmpty() ?
                                oppdaterStatus(progress, getInfoVenter(INNTK.getBeskrivelse()))
                                        .then(Mono.just("")) :
                                Mono.just("");
                    }
                })
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
                .flatMap(status -> oppdaterStatus(progress, status));
    }

    private Mono<String> importFraTenor(DollyPerson dollyPerson, BestillingProgress progress) {

        return transaksjonMappingService.existAlready(INNTK, dollyPerson.getIdent())
                .flatMap(exist -> {
                    if (isFalse(exist)) {
                        return inntektstubConsumer.sjekkImporterInntekt(dollyPerson.getIdent(), true)
                                .flatMap(checkResponse -> {
                                    if (checkResponse.getStatus().is2xxSuccessful()) {
                                        return oppdaterStatus(progress, getInfoVenter(INNTK.getBeskrivelse()))
                                                .then(inntektstubConsumer.sjekkImporterInntekt(dollyPerson.getIdent(), false)
                                                        .flatMap(importResponse -> {
                                                            if (importResponse.getStatus().is2xxSuccessful()) {
                                                                log.info("Import av inntektsdata fra Tenor for {} utført", dollyPerson.getIdent());
                                                                return oppdaterTransaksjonMapping(dollyPerson, progress, IMPORTERT_FRA_TENOR)
                                                                        .then(Mono.just("OK"));
                                                            } else {
                                                                log.error("Import av inntektsdata fra Tenor for {} feilet: {}",
                                                                        dollyPerson.getIdent(), importResponse.getMessage());
                                                                return Mono.just("Feil= " + ErrorStatusDecoder.encodeStatus(
                                                                        "Import av inntektsdata feilet: " + importResponse.getMessage()));
                                                            }
                                                        }));
                                    } else {
                                        log.info("Inntekt for {} finnes ikke i Tenor.", dollyPerson.getIdent());
                                        return oppdaterTransaksjonMapping(dollyPerson, progress, INGENDATA_FRA_TENOR)
                                                .then(Mono.just(""));
                                    }
                                });
                    } else {
                        return Mono.just("");
                    }
                });
    }

    private Mono<TransaksjonMapping> oppdaterTransaksjonMapping(DollyPerson dollyPerson,
                                                                BestillingProgress progress,
                                                                String status) {

        return transaksjonMappingService.save(TransaksjonMapping.builder()
                .ident(dollyPerson.getIdent())
                .system(INNTK.name())
                .transaksjonId(status)
                .bestillingId(progress.getBestillingId())
                .datoEndret(now())
                .build());
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
