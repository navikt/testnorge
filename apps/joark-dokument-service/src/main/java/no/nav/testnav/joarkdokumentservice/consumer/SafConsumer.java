package no.nav.testnav.joarkdokumentservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.joarkdokumentservice.config.credentias.TestnavSafProxyServiceProperties;
import no.nav.testnav.joarkdokumentservice.consumer.command.GetDokumentCommand;
import no.nav.testnav.joarkdokumentservice.consumer.command.GetDokumentInfoCommand;
import no.nav.testnav.joarkdokumentservice.consumer.command.GetPDFCommand;
import no.nav.testnav.joarkdokumentservice.domain.DokumentType;
import no.nav.testnav.joarkdokumentservice.domain.Journalpost;
import no.nav.testnav.libs.servletsecurity.config.NaisServerProperties;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class SafConsumer {
    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final NaisServerProperties properties;

    public SafConsumer(
            TestnavSafProxyServiceProperties properties,
            AccessTokenService accessTokenService
    ) {
        this.accessTokenService = accessTokenService;
        this.properties = properties;
        this.webClient = WebClient.builder().baseUrl(properties.getUrl()).build();
    }

    public Journalpost getJournalpost(Integer journalpostId, String miljo) {
        return accessTokenService
                .generateToken(properties)
                .flatMap(accessToken -> new GetDokumentInfoCommand(
                        webClient,
                        accessToken.getTokenValue(),
                        journalpostId,
                        miljo
                ).call())
                .map(response -> {
                    log.info("Response er: {}", response);
                    return new Journalpost(response.getData().getJournalpost());
                })
                .block();
    }

    public String getDokument(Integer journalpostId, Integer dokumentInfoId, DokumentType dokumentType, String miljo) {
        return accessTokenService
                .generateToken(properties)
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
        return accessTokenService
                .generateToken(properties)
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
