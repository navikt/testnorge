package no.nav.dolly.bestilling.pdldata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pdldata.command.PdlDataOppdateringCommand;
import no.nav.dolly.bestilling.pdldata.command.PdlDataOrdreCommand;
import no.nav.dolly.bestilling.pdldata.command.PdlDataSlettCommand;
import no.nav.dolly.config.credentials.PdlDataForvalterProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.service.TokenService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonUpdateRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class PdlDataConsumer {

    private final TokenService tokenService;
    private final WebClient webClient;
    private final PdlDataForvalterProperties properties;
    private final ObjectMapper objectMapper;

    public PdlDataConsumer(TokenService tokenService, PdlDataForvalterProperties serviceProperties, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.properties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
        this.objectMapper = objectMapper;
    }

    @Timed(name = "providers", tags = {"operation", "pdl_sendOrdre"})
    public Flux<String> sendOrdre(OrdreRequestDTO identer) {

        return tokenService.generateToken(properties)
                .flatMapMany(token -> new PdlDataOrdreCommand(webClient, identer, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "pdl_delete"})
    public void slettPdl(List<String> identer) {

        tokenService.generateToken(properties)
                .flatMapMany(token -> identer.stream()
                        .map(ident -> Flux.from(new PdlDataSlettCommand(webClient, ident, token.getTokenValue()).call()))
                        .reduce(Flux.empty(), Flux::concat))
                .collectList()
                .block();
    }

    public Mono<String> oppdaterPdl(String ident, PersonUpdateRequestDTO request) throws JsonProcessingException {

        var body = objectMapper.writeValueAsString(request);
        return tokenService.generateToken(properties)
                .flatMap(token ->
                        new PdlDataOppdateringCommand(webClient, ident, body, token.getTokenValue()).call());
    }
}
