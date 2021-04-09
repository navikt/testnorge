package no.nav.registre.testnorge.arbeidsforholdservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.arbeidsforholdservice.config.credentials.AaregServiceProperties;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.commnad.GetArbeidstakerArbeidsforholdCommand;
import no.nav.registre.testnorge.arbeidsforholdservice.domain.Arbeidsforhold;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Slf4j
@Component
public class AaregConsumer {

    private final WebClient webClient;
    private final NaisServerProperties serverProperties;
    private final AccessTokenService accessTokenService;

    public AaregConsumer(
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

    private List<Arbeidsforhold> getArbeidsforholds(String ident, String miljo) {
        var accessToken = accessTokenService.generateToken(serverProperties);
        return new GetArbeidstakerArbeidsforholdCommand(webClient, miljo, accessToken.getTokenValue(), ident).call();
    }

    private List<Arbeidsforhold> getArbeidsforholds(String ident, String orgnummer, String miljo) {
        return getArbeidsforholds(ident, miljo)
                .stream()
                .filter(Objects::nonNull)
                .filter(value -> value.getOrgnummer().equals(orgnummer))
                .collect(Collectors.toList());
    }

    public Arbeidsforhold getArbeidsforhold(String ident, String orgnummer, String arbeidsforholdId, String miljo) {
        return getArbeidsforholds(ident, orgnummer, miljo)
                .stream()
                .filter(value -> value.getArbeidsforholdId().equals(arbeidsforholdId))
                .findFirst()
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Klarer ikke aa finne arbeidsforhold for " + ident));
    }
}