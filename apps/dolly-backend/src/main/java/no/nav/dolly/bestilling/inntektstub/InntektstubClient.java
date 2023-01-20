package no.nav.dolly.bestilling.inntektstub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.dolly.bestilling.inntektstub.domain.InntektsinformasjonWrapper;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.util.TransactionHelperService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@Order(8)
@RequiredArgsConstructor
public class InntektstubClient implements ClientRegister {

    private final InntektstubConsumer inntektstubConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getInntektstub()) && !bestilling.getInntektstub().getInntektsinformasjon().isEmpty()) {

            var context = MappingContextUtils.getMappingContext();
            context.setProperty("ident", dollyPerson.getHovedperson());

            var inntektsinformasjonWrapper = mapperFacade.map(bestilling.getInntektstub(),
                    InntektsinformasjonWrapper.class, context);

            return Flux.from(inntektstubConsumer.getInntekter(dollyPerson.getHovedperson())
                    .collectList()
                    .flatMap(eksisterende -> Flux.fromIterable(inntektsinformasjonWrapper.getInntektsinformasjon())
                            .filter(nyinntekt -> eksisterende.stream().noneMatch(entry ->
                                    entry.getAarMaaned().equals(nyinntekt.getAarMaaned())))
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
                            })
                            .map(status -> futurePersist(progress, status))));
        }
        return Flux.empty();
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            progress.setInntektstubStatus(status);
            transactionHelperService.persister(progress);
            return progress;
        };
    }

    @Override
    public void release(List<String> identer) {

        inntektstubConsumer.deleteInntekter(identer)
                .subscribe(response -> log.info("Slettet identer fra Inntektstub"));
    }
}
