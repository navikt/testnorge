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
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.truncate;

@Slf4j
@Service
@RequiredArgsConstructor
public class InntektstubClient implements ClientRegister {

    private static final int MAX_STATUS_LEN = 2048;

    private final InntektstubConsumer inntektstubConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (isNull(bestilling.getInntektstub()) || bestilling.getInntektstub().getInntektsinformasjon().isEmpty()) {

            return Mono.empty();
        }

        var context = MappingContextUtils.getMappingContext();
        context.setProperty("ident", dollyPerson.getIdent());

        var inntektsinformasjonWrapper = mapperFacade.map(bestilling.getInntektstub(),
                InntektsinformasjonWrapper.class, context);

        return inntektstubConsumer.getInntekter(dollyPerson.getIdent())
                .collectList()
                .flatMap(eksisterende -> Flux.fromIterable(inntektsinformasjonWrapper.getInntektsinformasjon())
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
                                            .map(feil -> ErrorStatusDecoder.encodeStatus(errorStatusDecoder.getStatusMessage(feil)))
                                            .collect(Collectors.joining(","));
                        }))
                .flatMap(status -> oppdaterStatus(progress, status));
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
