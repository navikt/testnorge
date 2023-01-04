package no.nav.dolly.bestilling.inntektstub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.dolly.bestilling.inntektstub.domain.InntektsinformasjonWrapper;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.mapper.MappingContextUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@Order(8)
@RequiredArgsConstructor
public class InntektstubClient implements ClientRegister {

    private final InntektstubConsumer inntektstubConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;

    @Override
    public Flux<Void> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getInntektstub()) && !bestilling.getInntektstub().getInntektsinformasjon().isEmpty()) {

            var context = MappingContextUtils.getMappingContext();
            context.setProperty("ident", dollyPerson.getHovedperson());

            var inntektsinformasjonWrapper = mapperFacade.map(bestilling.getInntektstub(),
                    InntektsinformasjonWrapper.class, context);

            progress.setInntektstubStatus(
                    inntektstubConsumer.getInntekter(dollyPerson.getHovedperson())
                            .collectList()
                            .map(eksisterende -> Flux.fromIterable(inntektsinformasjonWrapper.getInntektsinformasjon())
                                    .filter(nyinntekt -> eksisterende.stream().noneMatch(entry ->
                                            entry.getAarMaaned().equals(nyinntekt.getAarMaaned())))
                                    .collectList()
                                    .map(inntekter -> inntektstubConsumer.postInntekter(inntekter)
                                            .collectList())
                                    .flatMap(Mono::from))
                            .flatMap(Mono::from)
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
                            .block());
        }
        return Flux.just();
    }

    @Override
    public void release(List<String> identer) {

        inntektstubConsumer.deleteInntekter(identer)
                .subscribe(response -> log.info("Slettet identer fra Inntektstub"));
    }

    @Override
    public boolean isDone(RsDollyBestilling kriterier, Bestilling bestilling) {

        return isNull(kriterier.getInntektstub()) ||
                bestilling.getProgresser().stream()
                        .allMatch(entry -> isNotBlank(entry.getInntektstubStatus()));
    }
}
