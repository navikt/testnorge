package no.nav.dolly.bestilling.tagshendelseslager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.tagshendelseslager.command.HendelseslagerPublishCommand;
import no.nav.dolly.bestilling.tagshendelseslager.command.TagsHenteCommand;
import no.nav.dolly.bestilling.tagshendelseslager.command.TagsOpprettingCommand;
import no.nav.dolly.bestilling.tagshendelseslager.command.TagsSlettingCommand;
import no.nav.dolly.config.credentials.PdlProxyProperties;
import no.nav.dolly.domain.resultset.Tags;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
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
    private final NaisServerProperties serviceProperties;

    public TagsHendelseslagerConsumer(TokenExchange tokenService, PdlProxyProperties serviceProperties, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .exchangeStrategies(JacksonExchangeStrategyUtil.getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "tags_create" })
    public String createTags(List<String> identer, List<Tags> tags) {

        String response = new TagsOpprettingCommand(webClient, identer, tags, serviceProperties.getAccessToken(tokenService)).call().block();
        log.info("Response for createTags: {}", response);

        return response;
    }

    @Timed(name = "providers", tags = { "operation", "tags_delete" })
    public void deleteTags(List<String> identer, List<Tags> tags) {

        new TagsSlettingCommand(webClient, identer, tags, serviceProperties.getAccessToken(tokenService)).call().block();
    }

    @Timed(name = "providers", tags = { "operation", "tags_get" })
    public JsonNode getTag(String ident) {

        return new TagsHenteCommand(webClient, ident, serviceProperties.getAccessToken(tokenService)).call().block();
    }

    @Timed(name = "providers", tags = { "operation", "hendelselager_publish" })
    public String publish(List<String> identer) {

        return new HendelseslagerPublishCommand(webClient, identer, serviceProperties.getAccessToken(tokenService)).call().block();
    }
}
