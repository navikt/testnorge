package no.nav.testnav.joarkdokumentservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.joarkdokumentservice.config.credentias.TestnavSafProxyServiceProperties;
import no.nav.testnav.joarkdokumentservice.consumer.command.GetDokumentCommand;
import no.nav.testnav.joarkdokumentservice.consumer.command.GetDokumentInfoCommand;
import no.nav.testnav.joarkdokumentservice.consumer.command.GetPDFCommand;
import no.nav.testnav.joarkdokumentservice.domain.DokumentType;
import no.nav.testnav.joarkdokumentservice.domain.Journalpost;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class SafConsumer {
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties properties;

    public SafConsumer(
            TestnavSafProxyServiceProperties properties,
            TokenExchange tokenExchange,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.tokenExchange = tokenExchange;
        this.properties = properties;
        this.webClient = WebClient.builder()
                .exchangeStrategies(
                        ExchangeStrategies.builder()
                                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024 * 50)).build())
                .baseUrl(properties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public Journalpost getJournalpost(String journalpostId, String miljo) {
        return tokenExchange
                .exchange(properties)
                .flatMap(accessToken -> new GetDokumentInfoCommand(
                        webClient,
                        accessToken.getTokenValue(),
                        journalpostId,
                        miljo
                ).call())
                .map(response -> {
                    log.info("Mottok journalpost: {}", response.getData());
                    if (!response.getErrors().isEmpty()) {
                        response.getErrors().forEach(error -> log.error("Feilet under henting av Journalpost: {}", error.getMessage()));
                    }
                    return new Journalpost(response.getData().getJournalpost());
                })
                .block();
    }

    public String getDokument(Integer journalpostId, Integer dokumentInfoId, DokumentType dokumentType, String miljo) {
        return tokenExchange
                .exchange(properties)
                .flatMap(accessToken -> new GetDokumentCommand(
                                webClient,
                                accessToken.getTokenValue(),
                                journalpostId,
                                dokumentInfoId,
                                miljo,
                                dokumentType
                        ).call()
                ).block();
    }

    public byte[] getPDF(Integer journalpostId, Integer dokumentInfoId, String miljo) {
        return tokenExchange
                .exchange(properties)
                .flatMap(accessToken -> new GetPDFCommand(
                                webClient,
                                accessToken.getTokenValue(),
                                journalpostId,
                                dokumentInfoId,
                                miljo
                        ).call()
                ).block();
    }
}
