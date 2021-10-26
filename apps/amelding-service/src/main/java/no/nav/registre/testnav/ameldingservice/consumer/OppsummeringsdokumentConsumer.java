package no.nav.registre.testnav.ameldingservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import no.nav.registre.testnav.ameldingservice.credentials.OppsummeringsdokumentServerProperties;
import no.nav.testnav.libs.commands.GetOppsummeringsdokumentByIdCommand;
import no.nav.testnav.libs.commands.GetOppsummeringsdokumentCommand;
import no.nav.testnav.libs.commands.SaveOppsummeringsdokumenterCommand;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.OppsummeringsdokumentDTO;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.Populasjon;
import no.nav.testnav.libs.reactivecore.config.ApplicationProperties;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;

@Component
public class OppsummeringsdokumentConsumer {
    private static final int BYTE_COUNT = 16 * 1024 * 1024;
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties properties;
    private final ApplicationProperties applicationProperties;

    public OppsummeringsdokumentConsumer(
            TokenExchange tokenExchange,
            OppsummeringsdokumentServerProperties properties,
            ObjectMapper objectMapper,
            ApplicationProperties applicationProperties
    ) {
        this.applicationProperties = applicationProperties;
        this.tokenExchange = tokenExchange;
        this.properties = properties;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .codecs(clientDefaultCodecsConfigurer -> {
                    clientDefaultCodecsConfigurer.defaultCodecs().maxInMemorySize(BYTE_COUNT);
                    clientDefaultCodecsConfigurer
                            .defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    clientDefaultCodecsConfigurer
                            .defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                })
                .build();
    }

    public Mono<String> save(OppsummeringsdokumentDTO dto, String miljo) {
        return tokenExchange
                .generateToken(properties)
                .flatMap(accessToken -> new SaveOppsummeringsdokumenterCommand(
                        webClient,
                        accessToken.getTokenValue(),
                        dto,
                        miljo,
                        applicationProperties.getName(),
                        Populasjon.DOLLY
                ).call());
    }


    public Mono<OppsummeringsdokumentDTO> get(String opplysningspliktigOrgnummer, LocalDate kalendermaaned, String miljo) {
        return tokenExchange
                .generateToken(properties)
                .flatMap(accessToken -> new GetOppsummeringsdokumentCommand(
                        webClient,
                        accessToken.getTokenValue(),
                        opplysningspliktigOrgnummer,
                        kalendermaaned,
                        miljo
                ).call());
    }

    public Mono<OppsummeringsdokumentDTO> get(String id) {
        return tokenExchange
                .generateToken(properties)
                .flatMap(accessToken -> new GetOppsummeringsdokumentByIdCommand(
                        webClient,
                        accessToken.getTokenValue(),
                        id
                ).call());
    }

}
