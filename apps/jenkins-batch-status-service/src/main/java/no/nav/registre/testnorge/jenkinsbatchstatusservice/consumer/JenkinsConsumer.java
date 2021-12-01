package no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.jenkinsbatchstatusservice.config.credentials.JenkinsServiceProperties;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command.GetBEREG007LogCommand;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command.GetQueueItemCommand;
import no.nav.testnav.libs.commands.GetCrumbCommand;
import no.nav.testnav.libs.dto.jenkins.v1.JenkinsCrumb;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Slf4j
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

    private JenkinsCrumb getCrumb() {
        var accessToken = tokenExchange.generateToken(properties).block();
        return new GetCrumbCommand(webClient, accessToken.getTokenValue()).call();
    }

    public Long getJobNumber(Long itemId) {
        var accessToken = tokenExchange.generateToken(properties).block();
        var dto = new GetQueueItemCommand(webClient, accessToken.getTokenValue(), getCrumb(), itemId).call();
        return dto.getNumber();
    }

    public String getJobLog(Long jobNumber) {
        var accessToken = tokenExchange.generateToken(properties).block();
        return new GetBEREG007LogCommand(webClient, accessToken.getTokenValue(), jobNumber).call();
    }
}