package no.nav.dolly.bestilling.inntektstub;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.dolly.bestilling.inntektstub.domain.InntektsinformasjonWrapper;
import no.nav.dolly.bestilling.inntektstub.util.CompareUtil;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
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

            var context = new MappingContext.Factory().getContext();
            context.setProperty("ident", dollyPerson.getHovedperson());

            var inntektsinformasjonWrapper = mapperFacade.map(bestilling.getInntektstub(),
                    InntektsinformasjonWrapper.class, context);

            progress.setInntektstubStatus(String.join(",",
                    requireNonNull(
                            inntektstubConsumer.getToken()
                                    .flatMapMany(token ->
                                            betingetOpprettInntekter(inntektsinformasjonWrapper.getInntektsinformasjon(), token))
                                    .collectList()
                                    .block())));
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

    private Mono<String> betingetOpprettInntekter(List<Inntektsinformasjon> inntektsinformasjon, AccessToken accessToken) {

        return inntektstubConsumer.getInntekter(inntektsinformasjon.get(0).getNorskIdent(), accessToken)
                .collectList()
                .map(inntekt -> CompareUtil.isSubsetOf(inntektsinformasjon, inntekt))
                .filter(BooleanUtils::isFalse)
                .flatMapMany(status -> inntektstubConsumer.postInntekter(inntektsinformasjon, accessToken))
                .collectList()
                .map(inntekter -> {
                    log.info("Inntektstub respons {}", inntekter);
                    return inntekter.stream()
                            .map(Inntektsinformasjon::getFeilmelding)
                            .noneMatch(StringUtils::isNotBlank) ? "OK" :
                            "Feil= " + inntekter.stream()
                                    .map(Inntektsinformasjon::getFeilmelding)
                                    .filter(StringUtils::isNotBlank)
                                    .map(feil -> encodeStatus(errorStatusDecoder.getStatusMessage(feil)))
                                    .collect(Collectors.joining(","));
                });
    }

    public Map<String, Object> status() {
        return inntektstubConsumer.checkStatus();
    }
}
