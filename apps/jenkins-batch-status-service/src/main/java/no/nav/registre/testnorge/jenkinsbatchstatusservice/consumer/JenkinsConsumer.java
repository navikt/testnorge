package no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.config.Consumers;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command.GetBEREG007LogCommand;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command.GetQueueItemCommand;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.dto.ItemDTO;
import no.nav.testnav.libs.commands.GetCrumbCommand;
import no.nav.testnav.libs.dto.jenkins.v1.JenkinsCrumb;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

    public Mono<Long> getJobNumber(Long itemId) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(accessToken -> {
                    String token = accessToken.getTokenValue();
                    return getCrumb(token)
                            .flatMap(crumb -> new GetQueueItemCommand(webClient, token, crumb, itemId).call());
                })
                .map(ItemDTO::getNumber);
    }

    public Mono<String> getJobLog(Long jobNumber) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(accessToken -> new GetBEREG007LogCommand(webClient, accessToken.getTokenValue(), jobNumber).call());
    }

    private Mono<JenkinsCrumb> getCrumb(String token) {
        return Mono.just(new GetCrumbCommand(webClient, token).call());
    }

}