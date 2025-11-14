package no.nav.testnav.apps.organisasjonbestillingservice.consumer;

import no.nav.testnav.apps.organisasjonbestillingservice.config.Consumers;
import no.nav.testnav.apps.organisasjonbestillingservice.consumer.command.GetBEREG007Command;
import no.nav.testnav.apps.organisasjonbestillingservice.consumer.command.GetBEREG007LogCommand;
import no.nav.testnav.apps.organisasjonbestillingservice.consumer.command.GetCrumbCommand;
import no.nav.testnav.apps.organisasjonbestillingservice.consumer.command.GetQueueItemCommand;
import no.nav.testnav.apps.organisasjonbestillingservice.consumer.dto.BuildDTO;
import no.nav.testnav.apps.organisasjonbestillingservice.consumer.dto.ItemDTO;
import no.nav.testnav.libs.dto.jenkins.v1.JenkinsCrumb;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class JenkinsConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public JenkinsConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        this.tokenExchange = tokenExchange;
        serverProperties = consumers.getTestnavDollyProxy();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl() + "/batch")
                .build();
    }

    private Mono<JenkinsCrumb> getCrumb(AccessToken accessToken) {
        return new GetCrumbCommand(webClient, accessToken.getTokenValue()).call();
    }

    public Mono<Long> getBuildId(Long itemId) {
        return tokenExchange
                .exchange(serverProperties)
                .flatMap(accessToken -> getCrumb(accessToken)
                        .flatMap(jenkinsCrumb -> new GetQueueItemCommand(webClient, accessToken.getTokenValue(), jenkinsCrumb, itemId).call())
                        .map(ItemDTO::getNumber)
                );
    }

    public Mono<String> getBuildLog(Long buildId) {
        return tokenExchange
                .exchange(serverProperties)
                .flatMap(accessToken -> new GetBEREG007LogCommand(webClient, accessToken.getTokenValue(), buildId).call());
    }

    public Mono<BuildDTO> getBuild(Long buildId) {
        return tokenExchange
                .exchange(serverProperties)
                .flatMap(accessToken -> new GetBEREG007Command(webClient, accessToken.getTokenValue(), buildId).call());
    }


}
