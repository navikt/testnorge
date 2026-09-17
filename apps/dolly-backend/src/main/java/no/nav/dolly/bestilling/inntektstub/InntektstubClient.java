package no.nav.dolly.bestilling.inntektstub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.dolly.bestilling.inntektstub.domain.InntektsinformasjonWrapper;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.service.TransactionHelperService;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.INNTK;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.truncate;

@Slf4j
@Service
@RequiredArgsConstructor
public class InntektstubClient implements ClientRegister {

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
                        return !bestilling.getInntekter().isEmpty() ||
                               nonNull(bestilling.getInntektstub()) && !bestilling.getInntektstub().getInntektsinformasjon().isEmpty() ?
                                oppdaterStatus(progress, getInfoVenter(INNTK.getBeskrivelse()))
                                        .then(Mono.just("")) :
                                Mono.just("");
                    }
                })
                .flatMap(status -> {

                    if (!bestilling.getInntekter().isEmpty()) {

                        return sendInntekterData(bestilling, dollyPerson);
                    } else if (nonNull(bestilling.getInntektstub()) && !bestilling.getInntektstub().getInntektsinformasjon().isEmpty()) {

                        return sendInntektstubData(bestilling, dollyPerson);
                    } else {
                        return Mono.just(status);
                    }
                })
                .flatMap(status -> isNotBlank(status) ? oppdaterStatus(progress, status) : Mono.empty());
    }

    private @NonNull Mono<String> sendInntekterData(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson) {

        var context = MappingContextUtils.getMappingContext();
        context.setProperty("ident", dollyPerson.getIdent());
        var nyeInntekter = mapperFacade.mapAsList(bestilling.getInntekter(), InntektsinformasjonWrapper.class, context)
                .stream()
                .map(InntektsinformasjonWrapper::getInntektsinformasjon)
                .flatMap(List::stream)
                .toList();

        return lagreInntekter(nyeInntekter, dollyPerson.getIdent());
    }

    private @NonNull Mono<String> sendInntektstubData(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson) {

        var context = MappingContextUtils.getMappingContext();
        context.setProperty("ident", dollyPerson.getIdent());
        var nyeInntekter = mapperFacade.map(bestilling.getInntektstub(), InntektsinformasjonWrapper.class, context)
                .getInntektsinformasjon();

        return lagreInntekter(nyeInntekter, dollyPerson.getIdent());
    }

    private @NonNull Mono<String> lagreInntekter(List<Inntektsinformasjon> nyeInntekter, String ident) {

        return inntektstubConsumer.getInntekter(ident)
                .collectList()
                .flatMap(eksisterende ->
                        Flux.fromIterable(nyeInntekter)
                                .filter(nyinntekt -> erNyInntekt(nyinntekt, eksisterende))
                                .collectList()
                                .flatMapMany(inntektstubConsumer::postInntekter)
                                .collectList()
                                .map(InntektstubClient::byggStatus));
    }

    private static boolean erNyInntekt(Inntektsinformasjon nyinntekt, List<Inntektsinformasjon> eksisterende) {

        return eksisterende.stream().noneMatch(entry ->
                entry.getAarMaaned().equals(nyinntekt.getAarMaaned()) &&
                entry.getVirksomhet().equals(nyinntekt.getVirksomhet()) &&
                entry.getInntektsliste().stream().anyMatch(gammelt -> nyinntekt.getInntektsliste().contains(gammelt)));
    }

    private static String byggStatus(List<Inntektsinformasjon> inntekter) {

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
