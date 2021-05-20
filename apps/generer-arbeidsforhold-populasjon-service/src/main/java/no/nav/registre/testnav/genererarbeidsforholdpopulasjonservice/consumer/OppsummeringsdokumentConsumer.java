package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command.GetOppsummeringsdokumentCommand;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command.SaveOppsummeringsdokumenterCommand;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.credentials.OppsummeringsdokuemntServerProperties;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding.Oppsummeringsdokument;
import no.nav.registre.testnorge.libs.common.command.GetOppsummeringsdokumenterByIdentCommand;
import no.nav.registre.testnorge.libs.common.command.GetOppsummeringsdokumenterCommand;
import no.nav.registre.testnorge.libs.core.config.ApplicationProperties;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.OppsummeringsdokumentDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.Populasjon;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Slf4j
@Component
public class OppsummeringsdokumentConsumer {
    private static final int BYTE_COUNT = 16 * 1024 * 1024;
    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final NaisServerProperties properties;
    private final ApplicationProperties applicationProperties;
    private final Executor executor;

    public OppsummeringsdokumentConsumer(
            AccessTokenService accessTokenService,
            OppsummeringsdokuemntServerProperties properties,
            ObjectMapper objectMapper,
            ApplicationProperties applicationProperties,
            Executor executor) {
        this.applicationProperties = applicationProperties;
        this.accessTokenService = accessTokenService;
        this.properties = properties;
        this.executor = executor;



        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
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

    @SneakyThrows
    public List<Mono<String>> saveAll(List<OppsummeringsdokumentDTO> list, String miljo) {
        var ids = new ArrayList<Mono<String>>();
        var futures = list.stream().map(dto -> saveFuture(dto, miljo)).collect(Collectors.toList());
        for (var future : futures) {
            ids.add(future.get());
        }
        return ids;
    }


    public Mono<String> save(OppsummeringsdokumentDTO dto, String miljo) {
        return accessTokenService
                .generateNonBlockedToken(properties)
                .flatMap(accessToken -> new SaveOppsummeringsdokumenterCommand(
                        webClient,
                        accessToken.getTokenValue(),
                        dto,
                        miljo,
                        applicationProperties.getName(),
                        Populasjon.MINI_NORGE
                ).call());
    }


    private CompletableFuture<Mono<String>> saveFuture(OppsummeringsdokumentDTO dto, String miljo) {
        AccessToken accessToken = accessTokenService.generateToken(properties);
        return CompletableFuture.supplyAsync(
                () -> save(dto, miljo),
                executor
        );
    }

    public List<OppsummeringsdokumentDTO> getAll(String miljo) {
        log.info("Henter alle oppsummeringsdokument fra {}...", miljo);
        AccessToken accessToken = accessTokenService.generateToken(properties);
        var list = new GetOppsummeringsdokumenterCommand(webClient, accessToken.getTokenValue(), miljo).call();
        log.info("Fant {} opplysningspliktig fra {}.", list.size(), miljo);
        return list;
    }

    public Mono<List<OppsummeringsdokumentDTO>> getAllForIdent(String ident, String miljo) {
        return accessTokenService.generateNonBlockedToken(properties)
                .flatMap(accessToken -> new GetOppsummeringsdokumenterByIdentCommand(webClient, accessToken.getTokenValue(), ident, miljo).call());
    }

    public Mono<Oppsummeringsdokument> getOppsummeringsdokument(String opplysningspliktigOrgnummer, LocalDate kalendermaaned, String miljo) {
        return accessTokenService
                .generateNonBlockedToken(properties)
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
