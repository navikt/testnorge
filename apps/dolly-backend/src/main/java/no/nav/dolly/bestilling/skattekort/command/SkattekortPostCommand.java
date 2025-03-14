package no.nav.dolly.bestilling.skattekort.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.RequestHeaderUtil;
import no.nav.testnav.libs.dto.skattekortservice.v1.IdentifikatorForEnhetEllerPerson;
import no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortRequestDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@Slf4j
public class SkattekortPostCommand implements Callable<Flux<String>> {

    private static final String SKATTEKORT_URL = "/api/v1/skattekort";
    private static final String CONSUMER = "Dolly";

    private final WebClient webClient;
    private final SkattekortRequestDTO request;
    private final String token;

    @Override
    public Flux<String> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(SKATTEKORT_URL).build())
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HEADER_NAV_CALL_ID, RequestHeaderUtil.getNavCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)
                .map(status -> getArbeidsgiverAndYear(request) + ErrorStatusDecoder.encodeStatus(status))
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty());
    }

    private static String getArbeidsgiverAndYear(SkattekortRequestDTO skattekort) {

        return skattekort.getArbeidsgiver().stream()
                .findFirst()
                .map(arbeidsgiver -> String.format("%s+%s:",
                        getArbeidsgiver(arbeidsgiver.getArbeidsgiveridentifikator()),
                        arbeidsgiver.getArbeidstaker().stream()
                                .findFirst()
                                .map(arbeidstaker -> arbeidstaker.getInntektsaar().toString())
                                .orElse("inntektsår")))
                .orElse("organisasjonsnummer+inntektsår:");
    }

    private static String getArbeidsgiver(IdentifikatorForEnhetEllerPerson identifikator) {

        return isNotBlank(identifikator.getOrganisasjonsnummer()) ?
                identifikator.getOrganisasjonsnummer() :
                identifikator.getPersonidentifikator();
    }
}
