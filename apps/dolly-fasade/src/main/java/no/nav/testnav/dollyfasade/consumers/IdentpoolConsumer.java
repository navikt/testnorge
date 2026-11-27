package no.nav.testnav.dollyfasade.consumers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollyfasade.config.Consumers;
import no.nav.testnav.dollyfasade.consumers.command.IdentpoolValidatorCommand;
import no.nav.testnav.libs.dto.identpool.v2.ValideringFlereRequestDTO;
import no.nav.testnav.libs.dto.identpool.v2.ValideringResponseDTO;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class IdentpoolConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;

    public IdentpoolConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            WebClient webClient) {

        this.tokenService = tokenService;
        serverProperties = consumers.getIdentPool();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Flux<ValideringResponseDTO> validerflere(@Valid @RequestBody ValideringFlereRequestDTO request) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new IdentpoolValidatorCommand(webClient, request, token.getTokenValue()).call());
    }
}
