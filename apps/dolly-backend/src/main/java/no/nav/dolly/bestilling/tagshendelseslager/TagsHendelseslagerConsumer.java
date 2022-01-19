package no.nav.dolly.bestilling.tagshendelseslager;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.tagshendelseslager.command.TagsOpprettingCommand;
import no.nav.dolly.bestilling.tagshendelseslager.command.TagsSlettingCommand;
import no.nav.dolly.config.credentials.PdlDataForvalterProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.util.JacksonExchangeStrategyUtil;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Service
public class TagsHendelseslagerConsumer {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final PdlDataForvalterProperties serviceProperties;

    public TagsHendelseslagerConsumer(TokenExchange tokenService, PdlDataForvalterProperties serviceProperties, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .exchangeStrategies(JacksonExchangeStrategyUtil.getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "tags_create"})
    public String createTags(List<String> identer, List<String> tags) {

        return new TagsOpprettingCommand(webClient, identer, tags, serviceProperties.getAccessToken(tokenService)).call().block();
    }

    @Timed(name = "providers", tags = {"operation", "tags_delete"})
    public void deleteTags(List<String> identer) {

        new TagsSlettingCommand(webClient, identer, serviceProperties.getAccessToken(tokenService)).call().block();
    }
}
