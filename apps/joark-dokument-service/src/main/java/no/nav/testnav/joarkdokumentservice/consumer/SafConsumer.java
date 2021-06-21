package no.nav.testnav.joarkdokumentservice.consumer;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import no.nav.testnav.joarkdokumentservice.config.credentias.TestnavSafProxyServiceProperties;
import no.nav.testnav.joarkdokumentservice.consumer.command.GetDokumentCommand;
import no.nav.testnav.joarkdokumentservice.consumer.command.GetDokumentInfoCommand;
import no.nav.testnav.joarkdokumentservice.domain.DokuemntType;
import no.nav.testnav.joarkdokumentservice.domain.DokumentInfo;

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

    public List<DokumentInfo> getDokumentInfo(Integer journalpostId, String miljo) {
        return accessTokenService
                .generateToken(properties)
                .flatMap(accessToken -> new GetDokumentInfoCommand(
                        webClient,
                        accessToken.getTokenValue(),
                        journalpostId,
                        miljo
                ).call())
                .map(response -> response.getData().getJournalpost().getDokumenter().stream().map(dokument ->
                        new DokumentInfo(response.getData().getJournalpost().getJournalpostId(), dokument)).collect(Collectors.toList())
                ).block();
    }

    public String getDokument(Integer journalpostId, Integer dokumentInfoId, DokuemntType dokuemntType, String miljo) {
        return accessTokenService
                .generateToken(properties)
                .flatMap(accessToken -> new GetDokumentCommand(
                        webClient,
                        accessToken.getTokenValue(),
                        journalpostId,
                        dokumentInfoId,
                        miljo,
                        dokuemntType
                ).call())
                .block();
    }
}
