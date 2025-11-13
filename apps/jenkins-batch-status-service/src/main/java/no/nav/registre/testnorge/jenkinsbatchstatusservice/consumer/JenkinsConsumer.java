package no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.config.Consumers;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command.GetBEREG007LogCommand;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command.GetQueueItemCommand;
import no.nav.testnav.libs.commands.GetCrumbCommand;
import no.nav.testnav.libs.dto.jenkins.v1.JenkinsCrumb;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Slf4j
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

    private JenkinsCrumb getCrumb() {
        return new GetCrumbCommand(webClient, getAccessTokenValue())
                .call();
    }

    public Long getJobNumber(Long itemId) {
        return new GetQueueItemCommand(webClient, getAccessTokenValue(), getCrumb(), itemId)
                .call()
                .getNumber();
    }

    public String getJobLog(Long jobNumber) {
        return new GetBEREG007LogCommand(webClient, getAccessTokenValue(), jobNumber)
                .call();
    }

    private String getAccessTokenValue() {
        return Optional.ofNullable(tokenExchange
                        .exchange(serverProperties)
                        .block())
                .orElseThrow(() -> new NullPointerException("Failed to retrieve access token"))
                .getTokenValue();
    }

}