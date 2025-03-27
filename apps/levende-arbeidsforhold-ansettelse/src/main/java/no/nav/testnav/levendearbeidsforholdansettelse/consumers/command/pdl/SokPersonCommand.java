package no.nav.testnav.levendearbeidsforholdansettelse.consumers.command.pdl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.dto.PdlPersonDTO;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.pdl.GraphqlVariables;
import no.nav.testnav.levendearbeidsforholdansettelse.provider.PdlMiljoer;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import static no.nav.testnav.levendearbeidsforholdansettelse.consumers.PdlConsumer.hentQueryResource;
import static no.nav.testnav.levendearbeidsforholdansettelse.consumers.command.pdl.TemaGrunnlag.GEN;
import static no.nav.testnav.levendearbeidsforholdansettelse.domain.pdl.CommonKeysAndUtils.*;

@RequiredArgsConstructor
@Slf4j
public class SokPersonCommand implements Callable<Flux<PdlPersonDTO>> {

    private static final String TEMA = "Tema";
    private static final String GRAPHQL_URL = "/graphql";
    private static final String PDL_API_URL = "/pdl-api";
    private static final String SOK_PERSON_QUERY = "pdl/sokPerson.graphql";

    private final WebClient webClient;
    private final GraphqlVariables.Paging paging;
    private final GraphqlVariables.Criteria criteria;
    private final String token;
    private final PdlMiljoer pdlMiljoe;

    @Override
    public Flux<PdlPersonDTO> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PDL_API_URL)
                        .path(pdlMiljoe.equals(PdlMiljoer.Q2) ? "" : "-" + pdlMiljoe.name().toLowerCase())
                        .path(GRAPHQL_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .header(HEADER_NAV_CONSUMER_ID, DOLLY)
                .header(HEADER_NAV_CALL_ID, "Dolly: " + UUID.randomUUID())
                .header(TEMA, GEN.name())
                .body(BodyInserters
                        .fromValue(new GraphQLRequest(hentQueryResource(SOK_PERSON_QUERY),
                                Map.of("paging", paging, "criteria", criteria))))
                .retrieve()
                .bodyToFlux(PdlPersonDTO.class)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty());
    }

}

