package no.nav.testnav.apps.organisasjonbestillingservice.consumer;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.organisasjonbestillingservice.consumer.command.GetBEREG007Command;
import no.nav.testnav.apps.organisasjonbestillingservice.consumer.command.GetBEREG007LogCommand;
import no.nav.testnav.apps.organisasjonbestillingservice.consumer.command.GetCrumbCommand;
import no.nav.testnav.apps.organisasjonbestillingservice.consumer.command.GetQueueItemCommand;
import no.nav.testnav.apps.organisasjonbestillingservice.consumer.dto.BuildDTO;
import no.nav.testnav.apps.organisasjonbestillingservice.consumer.dto.ItemDTO;
import no.nav.testnav.apps.organisasjonbestillingservice.credentials.JenkinsServiceProperties;
import no.nav.testnav.libs.dto.jenkins.v1.JenkinsCrumb;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Component
public class JenkinsConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties properties;

    public JenkinsConsumer(
            JenkinsServiceProperties properties,
            TokenExchange tokenExchange
    ) {
        this.tokenExchange = tokenExchange;
        this.properties = properties;
        this.webClient = WebClient.builder().baseUrl(properties.getUrl()).build();
    }

    private Mono<JenkinsCrumb> getCrumb(AccessToken accessToken) {
        return new GetCrumbCommand(webClient, accessToken.getTokenValue()).call();
    }

    public Mono<Long> getBuildId(Long itemId) {
        return tokenExchange
                .exchange(properties)
                .flatMap(accessToken -> getCrumb(accessToken)
                        .flatMap(jenkinsCrumb -> new GetQueueItemCommand(webClient, accessToken.getTokenValue(), jenkinsCrumb, itemId).call())
                        .map(ItemDTO::getNumber)
                );
    }

    public Mono<String> getBuildLog(Long buildId) {
        return tokenExchange
                .exchange(properties)
                .flatMap(accessToken -> new GetBEREG007LogCommand(webClient, accessToken.getTokenValue(), buildId).call());
    }

    public Mono<BuildDTO> getBuild(Long buildId) {
        return tokenExchange
                .exchange(properties)
                .flatMap(accessToken -> new GetBEREG007Command(webClient, accessToken.getTokenValue(), buildId).call());
    }


}
