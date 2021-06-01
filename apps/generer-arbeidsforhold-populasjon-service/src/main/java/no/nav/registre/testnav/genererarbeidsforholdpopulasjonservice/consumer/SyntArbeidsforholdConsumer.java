package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command.GenererArbeidsforholdHistorikkCommand;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command.GenererStartArbeidsforholdCommand;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.credentials.SyntrestServiceProperties;
import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdRequest;
import no.nav.registre.testnorge.libs.dto.syntrest.v1.ArbeidsforholdResponse;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Component
public class SyntArbeidsforholdConsumer {
    private final AccessTokenService accessTokenService;
    private final NaisServerProperties properties;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public SyntArbeidsforholdConsumer(
            AccessTokenService accessTokenService,
            SyntrestServiceProperties properties,
            ObjectMapper objectMapper
    ) {
        this.accessTokenService = accessTokenService;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .codecs(clientDefaultCodecsConfigurer -> {
                    clientDefaultCodecsConfigurer
                            .defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    clientDefaultCodecsConfigurer
                            .defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                })
                .build();
    }

    public Mono<List<ArbeidsforholdResponse>> genererStartArbeidsforhold(LocalDate startdato) {
        return accessTokenService
                .generateNonBlockedToken(properties)
                .flatMap(accessToken -> new GenererStartArbeidsforholdCommand(webClient, startdato, accessToken.getTokenValue()).call());
    }

    public Mono<List<List<ArbeidsforholdResponse>>> genererArbeidsforholdHistorikk(List<ArbeidsforholdRequest> requests) {
        return accessTokenService
                .generateNonBlockedToken(properties)
                .flatMap(accessToken -> new GenererArbeidsforholdHistorikkCommand(webClient, requests, accessToken.getTokenValue(), objectMapper).call());
    }
}
