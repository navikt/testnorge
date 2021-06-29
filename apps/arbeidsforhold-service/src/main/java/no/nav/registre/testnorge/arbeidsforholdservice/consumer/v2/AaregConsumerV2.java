package no.nav.registre.testnorge.arbeidsforholdservice.consumer.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arbeidsforholdservice.config.credentials.AaregServiceProperties;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.command.v2.GetArbeidstakerArbeidsforholdCommandV2;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.ArbeidsforholdDTO;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Component
public class AaregConsumerV2 {

    private final WebClient webClient;
    private final NaisServerProperties serverProperties;
    private final AccessTokenService accessTokenService;

    public AaregConsumerV2(
            AaregServiceProperties serverProperties,
            AccessTokenService accessTokenService,
            ObjectMapper objectMapper
    ) {
        this.serverProperties = serverProperties;
        this.accessTokenService = accessTokenService;

        ExchangeStrategies jacksonStrategy = ExchangeStrategies.builder()
                .codecs(config -> {
                    config.defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    config.defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                }).build();

        this.webClient = WebClient
                .builder()
                .exchangeStrategies(jacksonStrategy)
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public List<ArbeidsforholdDTO> getArbeidsforholds(String ident, String miljo) {
        var accessToken = accessTokenService.generateToken(serverProperties).block();

        return new GetArbeidstakerArbeidsforholdCommandV2(webClient, miljo, accessToken.getTokenValue(), ident).call();
    }
}