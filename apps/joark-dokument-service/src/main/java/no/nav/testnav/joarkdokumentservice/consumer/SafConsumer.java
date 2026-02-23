package no.nav.testnav.joarkdokumentservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.joarkdokumentservice.config.Consumers;
import no.nav.testnav.joarkdokumentservice.consumer.command.GetDokumentCommand;
import no.nav.testnav.joarkdokumentservice.consumer.command.GetDokumentInfoCommand;
import no.nav.testnav.joarkdokumentservice.consumer.command.GetPDFCommand;
import no.nav.testnav.joarkdokumentservice.consumer.dto.JournalpostDTO;
import no.nav.testnav.joarkdokumentservice.domain.DokumentType;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static java.util.Objects.nonNull;

@Slf4j
@Component
public class SafConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public SafConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        this.tokenExchange = tokenExchange;
        serverProperties = consumers.getTestnavDollyProxy();
        this.webClient = webClient
                .mutate()
                .exchangeStrategies(
                        ExchangeStrategies
                                .builder()
                                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024 * 50))
                                .build())
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<JournalpostDTO> getJournalpost(String journalpostId, String miljo) {
        return tokenExchange
                .exchange(serverProperties)
                .flatMap(accessToken -> new GetDokumentInfoCommand(
                        webClient,
                        accessToken.getTokenValue(),
                        journalpostId,
                        miljo
                ).call())
                .map(response -> {
                    log.info("Mottok journalpost: {}", response.getData());
                    if (nonNull(response.getErrors()) && !response.getErrors().isEmpty()) {
                        response.getErrors().forEach(error -> log.error("Feilet under henting av Journalpost: {}", error.getMessage()));
                    }
                    return response.getData().getJournalpost();
                });
    }

    public Mono<String> getDokument(String journalpostId, String dokumentInfoId, DokumentType dokumentType, String miljo) {
        return tokenExchange
                .exchange(serverProperties)
                .flatMap(accessToken -> new GetDokumentCommand(
                                webClient,
                                accessToken.getTokenValue(),
                                journalpostId,
                                dokumentInfoId,
                                miljo,
                                dokumentType
                        ).call()
                );
    }

    public Mono<byte[]> getPDF(String journalpostId, String dokumentInfoId, String miljo) {
        return tokenExchange
                .exchange(serverProperties)
                .flatMap(accessToken -> new GetPDFCommand(
                                webClient,
                                accessToken.getTokenValue(),
                                journalpostId,
                                dokumentInfoId,
                                miljo
                        ).call()
                );
    }
}
