package no.nav.testnav.apps.statusfrontend.fagsystem.technical;

import no.nav.testnav.apps.statusfrontend.config.Consumers;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PdlFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.SecondBatchTechnicalStatusProperties;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class SecondBatchTechnicalStatusClient {

    private final TokenExchange tokenExchange;
    private final ServerProperties dollyProxyProperties;
    private final ServerProperties arbeidsplassenCvProxyProperties;
    private final ServerProperties organisasjonForvalterProperties;
    private final PdlFunctionalTestProperties pdlProperties;
    private final SecondBatchTechnicalStatusProperties properties;
    private final WebClient webClient;

    public SecondBatchTechnicalStatusClient(
            TokenExchange tokenExchange,
            Consumers consumers,
            PdlFunctionalTestProperties pdlProperties,
            SecondBatchTechnicalStatusProperties properties,
            WebClient webClient
    ) {
        this.tokenExchange = tokenExchange;
        this.dollyProxyProperties = consumers.getTestnavDollyProxy();
        this.arbeidsplassenCvProxyProperties = consumers.getTestnavArbeidsplassenCVProxy();
        this.organisasjonForvalterProperties = consumers.getTestnavOrganisasjonForvalter();
        this.pdlProperties = pdlProperties;
        this.properties = properties;
        this.webClient = webClient;
    }

    public Mono<Void> checkArbeidsplassenCvProxy() {
        return checkReadiness(arbeidsplassenCvProxyProperties);
    }

    public Mono<Void> checkDollyProxy() {
        return checkReadiness(dollyProxyProperties);
    }

    public Mono<Void> checkOrganisasjonForvalter() {
        return checkReadiness(organisasjonForvalterProperties);
    }

    public Mono<Void> checkMedl() {
        var client = webClient.mutate()
                .baseUrl(dollyProxyProperties.getUrl())
                .build();
        return tokenExchange.exchange(dollyProxyProperties)
                .flatMap(token -> client.get()
                        .uri("/medl/rest/v1/person/{ident}", pdlProperties.getIdent())
                        .headers(headers -> headers.setBearerAuth(token.getTokenValue()))
                        .exchangeToMono(response -> {
                            if (response.statusCode().is2xxSuccessful()
                                    || response.statusCode() == HttpStatus.NOT_FOUND) {
                                return response.releaseBody();
                            }
                            return response.createException()
                                    .flatMap(exception -> Mono.<Void>error(exception));
                        }))
                .timeout(properties.getRequestTimeout());
    }

    public Mono<Void> checkFullmakt(RunId runId) {
        var client = webClient.mutate()
                .baseUrl(dollyProxyProperties.getUrl())
                .build();
        return tokenExchange.exchange(dollyProxyProperties)
                .flatMap(token -> client.get()
                        .uri("/fullmakt/api/fullmaktsgiver")
                        .headers(headers -> headers.setBearerAuth(token.getTokenValue()))
                        .header("Nav-Call-Id", runId.value().toString())
                        .header("Nav-Consumer-Id", "Dolly")
                        .header("fnr", pdlProperties.getIdent())
                        .exchangeToMono(response -> {
                            if (response.statusCode().is2xxSuccessful()
                                    || response.statusCode() == HttpStatus.NOT_FOUND) {
                                return response.releaseBody();
                            }
                            return response.createException()
                                    .flatMap(exception -> Mono.<Void>error(exception));
                        }))
                .timeout(properties.getRequestTimeout());
    }

    private Mono<Void> checkReadiness(ServerProperties serverProperties) {
        return webClient.mutate()
                .baseUrl(serverProperties.getUrl())
                .build()
                .get()
                .uri("/internal/health/readiness")
                .retrieve()
                .toBodilessEntity()
                .timeout(properties.getRequestTimeout())
                .then();
    }
}
