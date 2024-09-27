package no.nav.registre.testnav.ameldingservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnav.ameldingservice.config.Consumers;
import no.nav.testnav.libs.commands.GetOppsummeringsdokumentByIdCommand;
import no.nav.testnav.libs.commands.GetOppsummeringsdokumentCommand;
import no.nav.testnav.libs.commands.SaveOppsummeringsdokumenterCommand;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.OppsummeringsdokumentDTO;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.Populasjon;
import no.nav.testnav.libs.reactivecore.config.ApplicationProperties;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Slf4j
@Service
public class OppsummeringsdokumentConsumer {
    private static final int BYTE_COUNT = 16 * 1024 * 1024;
    private final WebClient webClient;
    private final ApplicationProperties applicationProperties;

    public OppsummeringsdokumentConsumer(
            Consumers consumers,
            ObjectMapper objectMapper,
            ApplicationProperties applicationProperties,
            WebClient.Builder webClientBuilder) {

        this.applicationProperties = applicationProperties;
        this.webClient = webClientBuilder
                .baseUrl(consumers.getOppsummeringsdokumentService().getUrl())
                .codecs(
                        clientDefaultCodecsConfigurer -> {
                            clientDefaultCodecsConfigurer
                                    .defaultCodecs()
                                    .maxInMemorySize(BYTE_COUNT);
                            clientDefaultCodecsConfigurer
                                    .defaultCodecs()
                                    .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                            clientDefaultCodecsConfigurer
                                    .defaultCodecs()
                                    .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                        })
                .build();
    }

    public Mono<String> save(OppsummeringsdokumentDTO dto, String miljo, AccessToken accessToken) {

        log.info("Dokument til innsending: {}", Json.pretty(dto));

        return new SaveOppsummeringsdokumenterCommand(
                webClient,
                accessToken.getTokenValue(),
                dto,
                miljo,
                applicationProperties.getName(),
                Populasjon.DOLLY
        ).call();
    }


    public Mono<OppsummeringsdokumentDTO> get(String opplysningspliktigOrgnummer, LocalDate kalendermaaned, String miljo, AccessToken accessToken) {
        return new GetOppsummeringsdokumentCommand(
                webClient,
                accessToken.getTokenValue(),
                opplysningspliktigOrgnummer,
                kalendermaaned,
                miljo)
                .call()
                .doOnNext(response -> log.info("Eksisterende dokument: {}", response));
    }

    public Mono<OppsummeringsdokumentDTO> get(String id, Mono<String> accessToken) {
        return
                accessToken.flatMap(token -> new GetOppsummeringsdokumentByIdCommand(
                        webClient,
                        token,
                        id
                ).call());
    }

}
