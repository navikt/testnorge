package no.nav.dolly.bestilling.pdldata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pdldata.command.PdlDataOppdateringCommand;
import no.nav.dolly.bestilling.pdldata.command.PdlDataOrdreCommand;
import no.nav.dolly.bestilling.pdldata.command.PdlDataSlettCommand;
import no.nav.dolly.config.credentials.PdlDataForvalterProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonUpdateRequestDTO;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PdlDataConsumer {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final PdlDataForvalterProperties serviceProperties;
    private final ObjectMapper objectMapper;

    public PdlDataConsumer(TokenExchange tokenService, PdlDataForvalterProperties serviceProperties, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
        this.objectMapper = objectMapper;
    }

    @Timed(name = "providers", tags = { "operation", "pdl_sendOrdre" })
    public String sendOrdre(String ident) {

        return new PdlDataOrdreCommand(webClient, ident, serviceProperties.getAccessToken(tokenService)).call()
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "pdl_delete" })
    public void slettPdl(List<String> identer) {

        String accessToken = serviceProperties.getAccessToken(tokenService);
        identer.stream()
                .map(ident -> Flux.from(new PdlDataSlettCommand(webClient, ident, accessToken).call()))
                .reduce(Flux.empty(), Flux::concat)
                .collectList()
                .block();
    }

    public String oppdaterPdl(String ident, PersonUpdateRequestDTO request) throws JsonProcessingException {

        var body = objectMapper.writeValueAsString(request);
        return new PdlDataOppdateringCommand(webClient, ident, body, serviceProperties.getAccessToken(tokenService)).call().block();
    }

    @Timed(name = "providers", tags = { "operation", "pdl_alive" })
    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }
}
