package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.config.Consumers;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command.GetOppsummeringsdokumentCommand;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command.SaveOppsummeringsdokumenterCommand;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding.Oppsummeringsdokument;
import no.nav.testnav.libs.commands.GetOppsummeringsdokumenterByIdentCommand;
import no.nav.testnav.libs.commands.GetOppsummeringsdokumenterCommand;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.OppsummeringsdokumentDTO;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.Populasjon;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletcore.config.ApplicationProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class OppsummeringsdokumentConsumer {
    private static final int BYTE_COUNT = 16 * 1024 * 1024;
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final ApplicationProperties applicationProperties;
    private final Executor executor;

    public OppsummeringsdokumentConsumer(
            TokenExchange tokenExchange,
            Consumers consumers,
            ObjectMapper objectMapper,
            ApplicationProperties applicationProperties,
            WebClient.Builder webClientBuilder) {

        this.applicationProperties = applicationProperties;
        this.tokenExchange = tokenExchange;
        serverProperties = consumers.getOppsummeringsdokumentService();
        this.executor = Executors.newFixedThreadPool(20);

        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .codecs(clientDefaultCodecsConfigurer -> {
                    clientDefaultCodecsConfigurer.defaultCodecs().maxInMemorySize(BYTE_COUNT);
                    clientDefaultCodecsConfigurer
                            .defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    clientDefaultCodecsConfigurer
                            .defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                })
                .build();
    }

    /**
     * Bruker future til å sette en limit på antall samtidige requests.
     */
    @SneakyThrows
    public Mono<String> save(OppsummeringsdokumentDTO dto, String miljo) {
        return Mono.fromFuture(saveFuture(dto, miljo));
    }

    private CompletableFuture<String> saveFuture(OppsummeringsdokumentDTO dto, String miljo) {
        return CompletableFuture.supplyAsync(
                () -> tokenExchange
                        .exchange(serverProperties)
                        .flatMap(accessToken -> new SaveOppsummeringsdokumenterCommand(
                                webClient,
                                accessToken.getTokenValue(),
                                dto,
                                miljo,
                                applicationProperties.getName(),
                                Populasjon.MINI_NORGE
                        ).call())
                        .block(),
                executor
        );
    }

    public List<OppsummeringsdokumentDTO> getAll(String miljo) {
        log.info("Henter alle oppsummeringsdokument fra {}...", miljo);
        var accessToken = tokenExchange.exchange(serverProperties).block();
        var list = new GetOppsummeringsdokumenterCommand(webClient, accessToken.getTokenValue(), miljo).call();
        log.info("Fant {} opplysningspliktig fra {}.", list.size(), miljo);
        return list;
    }

    public Mono<List<OppsummeringsdokumentDTO>> getAllForIdent(String ident, String miljo) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(accessToken -> new GetOppsummeringsdokumenterByIdentCommand(webClient, accessToken.getTokenValue(), ident, miljo).call());
    }

    public Mono<Oppsummeringsdokument> getOppsummeringsdokument(String opplysningspliktigOrgnummer, LocalDate kalendermaaned, String miljo) {
        return tokenExchange
                .exchange(serverProperties)
                .flatMap(accessToken -> new GetOppsummeringsdokumentCommand(
                        webClient,
                        accessToken.getTokenValue(),
                        opplysningspliktigOrgnummer,
                        kalendermaaned,
                        miljo
                ).call())
                .defaultIfEmpty(
                        OppsummeringsdokumentDTO
                                .builder()
                                .version(1L)
                                .kalendermaaned(kalendermaaned)
                                .opplysningspliktigOrganisajonsnummer(opplysningspliktigOrgnummer)
                                .virksomheter(new ArrayList<>())
                                .build()
                ).map(Oppsummeringsdokument::new);
    }
}
