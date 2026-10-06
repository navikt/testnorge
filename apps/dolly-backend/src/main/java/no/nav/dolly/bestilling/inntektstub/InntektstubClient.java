package no.nav.dolly.bestilling.inntektstub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.inntektstub.InntektMultiplierWrapper;
import no.nav.dolly.domain.resultset.inntektstub.RsInntekter;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.service.TransactionHelperService;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.INNTK;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.truncate;

@Slf4j
@Service
@RequiredArgsConstructor
public class InntektstubClient implements ClientRegister {

    private static final DateTimeFormatter YEAR_MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final int MAX_STATUS_LEN = 200;

    private final InntektstubConsumer inntektstubConsumer;
    private final MapperFacade mapperFacade;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        return Mono.just(bestilling)
                .flatMap(_ -> {
                    if (dollyPerson.isTestnorgeIdent()) {
                        return importFraTenor(dollyPerson, progress);
                    } else {
                        return nonNull(bestilling.getInntektstub()) && !bestilling.getInntektstub().getInntektsinformasjon().isEmpty() ?
                                oppdaterStatus(progress, getInfoVenter(INNTK.getBeskrivelse()))
                                        .then(Mono.just("")) :
                                Mono.just("");
                    }
                })
                .flatMap(status -> {

                    if (!bestilling.getInntekter().isEmpty()) {
                        return mapInntekterData(bestilling.getInntekter(), dollyPerson)
                                .zipWith(Mono.just(status));

                    } else if (nonNull(bestilling.getInntektstub()) && !bestilling.getInntektstub().getInntektsinformasjon().isEmpty()) {
                        return mapInntektsinformasjonWrapper(bestilling.getInntektstub(), dollyPerson)
                                .zipWith(Mono.just(status));
                    }
                    return Mono.just("").zipWith(Mono.just(status));
                })
                .map(tuple -> {
                    if (isNotBlank(tuple.getT1())) {
                        return "%s,%s".formatted(tuple.getT2(), tuple.getT1());
                    } else {
                        return tuple.getT2();
                    }
                })
                .flatMap(status -> status.length() > 1 ? oppdaterStatus(progress, status) : Mono.empty());
    }

    private Mono<String> mapInntekterData(List<RsInntekter> inntekter, DollyPerson dollyPerson) {

        var nyeInntekter = inntekter.stream()
                .flatMap(inntekt -> inntekt.getPerioder().stream()
                        .map(periode -> {
                            var context = MappingContextUtils.getMappingContext();
                            context.setProperty("ident", dollyPerson.getIdent());
                            context.setProperty("periode", periode);
                            return mapperFacade.map(inntekt, Inntektsinformasjon.class, context);
                        }))
                .toList();

        return oppdaterInntektstub(dollyPerson, nyeInntekter);
    }

    private Mono<String> mapInntektsinformasjonWrapper(InntektMultiplierWrapper warpper, DollyPerson dollyPerson) {

        var nyeInntekter = warpper.getInntektsinformasjon().stream()
                .flatMap(inntekter -> {
                    var sisteAarMaaned = YearMonth.parse(inntekter.getSisteAarMaaned(), YEAR_MONTH_FORMAT);
                    return LongStream.range(0, nonNull(inntekter.getAntallMaaneder()) ?
                                    inntekter.getAntallMaaneder() : 1)
                            .mapToObj(sisteAarMaaned::minusMonths)
                            .map(yearMonth -> {
                                var context = MappingContextUtils.getMappingContext();
                                context.setProperty("ident", dollyPerson.getIdent());
                                context.setProperty("periode", yearMonth);
                                return mapperFacade.map(inntekter, Inntektsinformasjon.class, context);
                            });
                })
                .toList();

        return oppdaterInntektstub(dollyPerson, nyeInntekter);
    }

    private @NonNull Mono<String> oppdaterInntektstub(DollyPerson dollyPerson, List<Inntektsinformasjon> nyInntektsinformasjon) {
        return inntektstubConsumer.getInntekter(dollyPerson.getIdent())
                .collectList()
                .flatMap(eksisterende ->
                        Flux.fromIterable(nyInntektsinformasjon)
                                .filter(nyinntekt ->
                                        eksisterende.stream().noneMatch(entry ->
                                                nyinntekt.getAarMaaned().equals(entry.getAarMaaned()) &&
                                                nyinntekt.getVirksomhet().equals(entry.getVirksomhet()) &&
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
                                                    .distinct()
                                                    .collect(Collectors.joining(","));
                                }));
    }

    private Mono<String> importFraTenor(DollyPerson dollyPerson, BestillingProgress progress) {

        return inntektstubConsumer.sjekkImporterInntekt(dollyPerson.getIdent(), true)
                .flatMap(checkResponse -> {
                    if (checkResponse.getStatus().is2xxSuccessful()) {
                        return oppdaterStatus(progress, getInfoVenter(INNTK.getBeskrivelse()))
                                .then(inntektstubConsumer.sjekkImporterInntekt(dollyPerson.getIdent(), false)
                                        .flatMap(importResponse -> {
                                            if (importResponse.getStatus().is2xxSuccessful()) {
                                                log.info("Import av inntektsdata fra Tenor for {} utført", dollyPerson.getIdent());
                                                return Mono.just("OK");
                                            } else {
                                                log.error("Import av inntektsdata fra Tenor for {} feilet: {}",
                                                        dollyPerson.getIdent(), importResponse.getMessage());
                                                return Mono.just("Feil= " + ErrorStatusDecoder.encodeStatus(
                                                        "Import av inntektsdata feilet: " + importResponse.getMessage()));
                                            }
                                        }));
                    } else {
                        log.info("Inntekt for {} finnes ikke i Tenor.", dollyPerson.getIdent());
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
                .subscribe(_ -> log.info("Slettet identer fra Inntektstub"));
    }
}
