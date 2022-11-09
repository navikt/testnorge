package no.nav.dolly.consumer.pdlperson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.dolly.config.credentials.PdlProxyProperties;
import no.nav.dolly.consumer.pdlperson.command.PdlBolkPersonGetCommand;
import no.nav.dolly.consumer.pdlperson.command.PdlPersonGetCommand;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.apache.http.Consts;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class PdlPersonConsumer {

    private static final String PDL_API_URL = "/pdl-api";
    private static final int BLOCK_SIZE = 50;
    private final TokenExchange tokenService;
    private final NaisServerProperties serviceProperties;
    private final WebClient webClient;

    public PdlPersonConsumer(TokenExchange tokenService,
                             PdlProxyProperties serverProperties,
                             ObjectMapper objectMapper,
                             ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.serviceProperties = serverProperties;
        this.tokenService = tokenService;
        webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public JsonNode getPdlPerson(String ident) {

        return getPdlPerson(ident, PDL_MILJOER.Q2);
    }

    @Timed(name = "providers", tags = { "operation", "pdl_getPerson" })
    public JsonNode getPdlPerson(String ident, PDL_MILJOER pdlMiljoe) {

        return tokenService.exchange(serviceProperties)
                .flatMap((AccessToken token) -> new PdlPersonGetCommand(webClient, ident, token.getTokenValue(), pdlMiljoe)
                        .call()).block();
    }

    @Timed(name = "providers", tags = { "operation", "pdl_getPersoner" })
    public Flux<PdlPersonBolk> getPdlPersoner(List<String> identer) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> Flux.range(0, identer.size() / BLOCK_SIZE + 1)
                        .flatMap(index -> new PdlBolkPersonGetCommand(webClient,
                                identer.subList(index * BLOCK_SIZE, Math.min((index + 1) * BLOCK_SIZE, identer.size())),
                                token.getTokenValue()
                        ).call()));
    }

    public Map<String, String> checkAlive() {
        try {
            return Map.of(serviceProperties.getName() + PDL_API_URL, serviceProperties.checkIsAlive(webClient, serviceProperties.getAccessToken(tokenService)));
        } catch (SecurityException | WebClientResponseException ex) {
            log.error("{} feilet mot URL: {}", serviceProperties.getName(), serviceProperties.getUrl(), ex);
            return Map.of(serviceProperties.getName(), String.format("%s, URL: %s", ex.getMessage(), serviceProperties.getUrl()));
        }
    }

    public static String hentQueryResource(String pathResource) {
        val resource = new ClassPathResource(pathResource);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), Consts.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));

        } catch (IOException e) {
            log.error("Lesing av query ressurs {} feilet", pathResource, e);
            return null;
        }
    }

    public enum PDL_MILJOER {
        Q1, Q2
    }

    public Map<String, Object> checkStatus() {
        var statusMap =  CheckAliveUtil.checkConsumerStatus(
                serviceProperties.getUrl() + "/internal/isAlive",
                serviceProperties.getUrl() + "/internal/isReady",
                WebClient.builder().build());
        statusMap.put("team", "Dolly");
        statusMap.put("alive-url", serviceProperties.getUrl() + "/internal/isAlive");

        // "pdl-testdata" ikke direkte tilgang
        var pdlTestdataStatus = CheckAliveUtil.checkConsumerStatus(
                "https://pdl-testdata.dev.intern.nav.no/internal/health/liveness",
                "https://pdl-testdata.dev.intern.nav.no/internal/health/readiness",
                WebClient.builder().build());
        pdlTestdataStatus.put("team", "pdl");
        pdlTestdataStatus.put("alive-url", "https://pdl-testdata.dev.intern.nav.no/internal/health/liveness");

        // ikke direkte tilgang
        var pdlApiStatus = CheckAliveUtil.checkConsumerStatus(
                "https://pdl-api.dev.intern.nav.no/internal/health/liveness",
                "https://pdl-api.dev.intern.nav.no/internal/health/readiness",
                WebClient.builder().build());
        pdlApiStatus.put("team", "pdl");
        pdlApiStatus.put("alive-url", "https://pdl-api.dev.intern.nav.no/internal/health/liveness");

        // ikke direkte tilgang
        var pdlApiQ1Status = CheckAliveUtil.checkConsumerStatus(
                "https://pdl-api-q1.dev.intern.nav.no/internal/health/liveness",
                "https://pdl-api-q1.dev.intern.nav.no/internal/health/readiness",
                WebClient.builder().build());
        pdlApiQ1Status.put("team", "pdl");
        pdlApiQ1Status.put("alive-url", "https://pdl-api-q1.dev.intern.nav.no/internal/health/liveness");

        // ikke direkte tilgang
        var pdlIdenthendelseStatus = CheckAliveUtil.checkConsumerStatus(
                "https://pdl-identhendelse-lager.dev.intern.nav.no/internal/health/liveness",
                "https://pdl-identhendelse-lager.dev.intern.nav.no/internal/health/readiness",
                WebClient.builder().build());
        pdlIdenthendelseStatus.put("team", "pdl");
        pdlIdenthendelseStatus.put("alive-url", "https://pdl-identhendelse-lager.dev.intern.nav.no/internal/health/liveness");

        // ikke direkte tilgang
        var pdlAktorStatus = CheckAliveUtil.checkConsumerStatus(
                "https://pdl-aktor.dev.intern.nav.no/internal/health/liveness",
                "https://pdl-aktor.dev.intern.nav.no/internal/health/readiness",
                WebClient.builder().build());
        pdlAktorStatus.put("team", "pdl");
        pdlAktorStatus.put("alive-url", "https://pdl-aktor.dev.intern.nav.no/internal/health/liveness");

        // https://pdl-es-q.adeo.no


        return Map.of(
                "pdl-proxy", statusMap,
                "pdl-testdata", pdlTestdataStatus,
                "pdl-api", pdlApiStatus,
                "pdl-api-q1", pdlApiQ1Status,
                "pdl-identhendelse", pdlIdenthendelseStatus,
                "pdl-aktor", pdlAktorStatus
        );
    }
}
