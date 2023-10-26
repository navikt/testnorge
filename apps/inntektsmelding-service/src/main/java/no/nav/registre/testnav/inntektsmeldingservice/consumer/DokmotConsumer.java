package no.nav.registre.testnav.inntektsmeldingservice.consumer;

import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnav.inntektsmeldingservice.config.Consumers;
import no.nav.registre.testnav.inntektsmeldingservice.consumer.command.OpprettJournalpostCommand;
import no.nav.registre.testnav.inntektsmeldingservice.domain.FilLaster;
import no.nav.testnav.libs.dto.dokarkiv.v1.DokmotRequest;
import no.nav.testnav.libs.dto.dokarkiv.v1.InntektDokument;
import no.nav.testnav.libs.dto.dokarkiv.v1.ProsessertInntektDokument;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DokmotConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public DokmotConsumer(
            Consumers consumers,
            TokenExchange tokenExchange) {
        this.tokenExchange = tokenExchange;
        serverProperties = consumers.getTestnavDokarkivProxy();
        this.webClient = WebClient
                .builder()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public List<ProsessertInntektDokument> opprettJournalpost(String miljoe, List<InntektDokument> inntektDokumenter, String navCallId) {
        log.info("Oppretter {} journalpost(er) i miljø {} for inntektsdokument(er).\nNav-Call-Id: {}", inntektDokumenter.size(), miljoe, navCallId);
        var pdf = FilLaster.instans().hentDummyPDF();

        return tokenExchange
                .exchange(serverProperties)
                .flatMapMany(token -> Flux.concat(
                                inntektDokumenter
                                        .stream()
                                        .map(inntektDokument -> new OpprettJournalpostCommand(webClient, token.getTokenValue(), new DokmotRequest(inntektDokument, pdf), navCallId, miljoe)
                                                .call()
                                                .map(response -> {
                                                    log.info(
                                                            "Lagt inn dokument i joark med journalpostId: {} og eksternReferanseId: {}\n{}",
                                                            response.getJournalpostId(),
                                                            inntektDokument.getMetadata().getEksternReferanseId(),
                                                            Json.pretty(inntektDokument)
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
