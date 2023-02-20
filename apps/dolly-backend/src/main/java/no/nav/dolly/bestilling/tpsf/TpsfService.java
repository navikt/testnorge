package no.nav.dolly.bestilling.tpsf;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.config.credentials.TpsForvalterenProxyProperties;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.RsOppdaterPersonResponse;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.exceptions.TpsfException;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
public class TpsfService {

    private static final String TPSF_BASE_URL = "/api/v1/dolly/testdata";
    private static final String TPSF_OPPRETT_URL = "personer";
    private static final String TPSF_HENT_PERSONER_URL = "hentpersoner";
    private static final String TPSF_UPDATE_PERSON_URL = TPSF_BASE_URL + "/leggtilpaaperson";

    private static final String TPSF_IDENT_QUERY = "ident";
    private static final String TPSF = "TPSF: ";

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final NaisServerProperties serviceProperties;

    public TpsfService(TokenExchange tokenService,
                       TpsForvalterenProxyProperties serverProperties,
                       ObjectMapper objectMapper
    ) {

        this.tokenService = tokenService;
        this.objectMapper = objectMapper;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "tpsf_opprettIdenter" })
    public List<String> opprettIdenterTpsf(TpsfBestilling request) {

        var response = postToTpsf(TPSF_OPPRETT_URL, request);
        return isNull(response) ? null : objectMapper.convertValue(response, List.class);
    }

    @Timed(name = "providers", tags = { "operation", "tpsf_hentTestpersoner" })
    public List<Person> hentTestpersoner(List<String> identer) {

        if (identer.isEmpty()) {
            return emptyList();
        }

        var response = postToTpsf(TPSF_HENT_PERSONER_URL, identer);
        return isNull(response) ? emptyList() :
                new ArrayList<>(List.of(objectMapper.convertValue(response, Person[].class)));
    }

    @Timed(name = "providers", tags = { "operation", "tpsf_leggTIlPaaPerson" })
    public RsOppdaterPersonResponse endreLeggTilPaaPerson(String ident, TpsfBestilling tpsfBestilling) {

        var response = webClient.post().uri(uriBuilder -> uriBuilder
                        .path(TPSF_UPDATE_PERSON_URL)
                        .queryParam(TPSF_IDENT_QUERY, ident).build())
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .bodyValue(tpsfBestilling)
                .retrieve()
                .bodyToMono(RsOppdaterPersonResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();

        if (isNull(response)) {
            throw new DollyFunctionalException(String.format("Klarte ikke å legge til på ident %s i TPS-Forvalteren", ident));
        }
        return response;
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    private Object postToTpsf(String addtionalUrl, Object request) {

        return webClient.post().uri(uriBuilder -> uriBuilder
                        .path(TPSF_BASE_URL)
                        .pathSegment(addtionalUrl).build())
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Object.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(throwable -> {

                    if (throwable instanceof WebClientResponseException webClientResponseException) {
                        if (HttpStatus.BAD_REQUEST == webClientResponseException.getStatusCode()) {
                            return Mono.error(throwable);
                        } else if (HttpStatus.NOT_FOUND == webClientResponseException.getStatusCode()) {
                            return Mono.error(new NotFoundException(TPSF + getMessage(throwable)));
                        } else {
                            return Mono.error(new TpsfException(TPSF + getMessage(throwable)));
                        }
                    } else {

                        return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, TPSF + throwable.getMessage(),
                                throwable.getCause()));
                    }
                })
                .block();
    }

    protected static String getMessage(Throwable error) {

        return error instanceof WebClientResponseException webClientResponseException ?
                webClientResponseException.getResponseBodyAsString(StandardCharsets.UTF_8) :
                error.getMessage();
    }
}
