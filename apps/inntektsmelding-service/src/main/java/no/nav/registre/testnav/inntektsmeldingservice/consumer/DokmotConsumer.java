package no.nav.registre.testnav.inntektsmeldingservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnav.inntektsmeldingservice.config.credentials.DokarkivProxyServiceProperties;
import no.nav.registre.testnav.inntektsmeldingservice.consumer.command.OpprettJournalpostCommand;
import no.nav.registre.testnav.inntektsmeldingservice.domain.FilLaster;
import no.nav.registre.testnorge.libs.dto.dokarkiv.v1.DokmotRequest;
import no.nav.registre.testnorge.libs.dto.dokarkiv.v1.InntektDokument;
import no.nav.registre.testnorge.libs.dto.dokarkiv.v1.ProsessertInntektDokument;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Slf4j
@Component
public class DokmotConsumer {

    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final NaisServerProperties properties;

    public DokmotConsumer(
            DokarkivProxyServiceProperties properties,
            AccessTokenService accessTokenService
    ) {
        this.accessTokenService = accessTokenService;
        this.properties = properties;
        this.webClient = WebClient.builder().baseUrl(properties.getUrl()).build();
    }

    public List<ProsessertInntektDokument> opprettJournalpost(String miljoe, List<InntektDokument> inntektDokumenter, String navCallId) {
        log.info("Oppretter {} journalpost(er) i miljø {} for inntektsdokument(er).\nNav-Call-Id: {}", inntektDokumenter.size(), miljoe, navCallId);
        var pdf = FilLaster.instans().hentDummyPDF();

        return accessTokenService
                .generateToken(properties)
                .flatMapMany(token -> Flux.concat(
                        inntektDokumenter
                                .stream()
                                .map(inntektDokument -> new OpprettJournalpostCommand(webClient, token.getTokenValue(), new DokmotRequest(inntektDokument, pdf), navCallId, miljoe)
                                        .call()
                                        .map(response -> {
                                            log.info(
                                                    "Lagt inn dokument i joark med journalpostId: {} og eksternReferanseId: {}",
                                                    response.getJournalpostId(),
                                                    inntektDokument.getMetadata().getEksternReferanseId()
                                            );
                                            return new ProsessertInntektDokument(inntektDokument, response);
                                        })
                                ).collect(Collectors.toList())
                        )
                )
                .collectList()
                .block();
    }
}
