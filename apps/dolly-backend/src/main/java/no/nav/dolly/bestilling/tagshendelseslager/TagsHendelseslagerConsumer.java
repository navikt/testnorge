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
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Service
public class TagsHendelseslagerConsumer {

    private static final int BLOCK_SIZE = 100;
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

    @Timed(name = "providers", tags = {"operation", "tags_create"})
    public Flux<String> createTags(List<String> identer, List<Tags> tags) {

       return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> Flux.range(0, identer.size() / BLOCK_SIZE + 1)
                        .map(index -> new TagsOpprettingCommand(webClient,
                                identer.subList(index * BLOCK_SIZE, Math.min((index + 1) * BLOCK_SIZE, identer.size())),
                                tags, serviceProperties.getAccessToken(tokenService)).call())
                        .flatMap(Flux::from));
    }

    @Timed(name = "providers", tags = {"operation", "tags_delete"})
    public Flux<String> deleteTags(List<String> identer, List<Tags> tags) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> Flux.range(0, identer.size() / BLOCK_SIZE + 1)
                        .map(index -> new TagsSlettingCommand(webClient,
                                identer.subList(index * BLOCK_SIZE, Math.min((index + 1) * BLOCK_SIZE, identer.size())),
                                tags, token.getTokenValue()).call())
                        .flatMap(Flux::from));
    }

    @Timed(name = "providers", tags = {"operation", "tags_get"})
    public JsonNode getTag(String ident) {

        return new TagsHenteCommand(webClient, ident, serviceProperties.getAccessToken(tokenService)).call().block();
    }

    @Timed(name = "providers", tags = {"operation", "hendelselager_publish"})
    public String publish(List<String> identer) {

        return new HendelseslagerPublishCommand(webClient, identer, serviceProperties.getAccessToken(tokenService)).call().block();
    }
}
