package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.config.credentials.OppsummeringsdokuemntServerProperties;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Opplysningspliktig;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Organisajon;
import no.nav.testnav.libs.commands.GetOppsummeringsdokumentCommand;
import no.nav.testnav.libs.commands.GetOppsummeringsdokumenterCommand;
import no.nav.testnav.libs.commands.SaveOppsummeringsdokumenterCommand;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.Populasjon;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletcore.config.ApplicationProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Slf4j
@Component
public class OppsummeringsdokumentConsumer {
    private static final int BYTE_COUNT = 16 * 1024 * 1024;
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties properties;
    private final Executor executor;
    private final ApplicationProperties applicationProperties;

    public OppsummeringsdokumentConsumer(
            TokenExchange tokenExchange,
            OppsummeringsdokuemntServerProperties properties,
            ObjectMapper objectMapper,
            ApplicationProperties applicationProperties
    ) {
        this.applicationProperties = applicationProperties;
        this.executor = Executors.newFixedThreadPool(10);
        this.tokenExchange = tokenExchange;
        this.properties = properties;
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

    private CompletableFuture<String> saveOpplysningspliktig(Opplysningspliktig opplysningspliktig, String miljo) {
        AccessToken accessToken = tokenExchange.generateToken(properties).block();
        return CompletableFuture.supplyAsync(
                () -> new SaveOppsummeringsdokumenterCommand(
                        webClient,
                        accessToken.getTokenValue(),
                        opplysningspliktig.toDTO(),
                        miljo,
                        applicationProperties.getName(),
                        Populasjon.MINI_NORGE
                ).call().block(),
                executor
        );
    }

    public Optional<Opplysningspliktig> getOpplysningspliktig(Organisajon organisajon, LocalDate kalendermaaned, String miljo) {
        AccessToken accessToken = tokenExchange.generateToken(properties).block();
        var dto = new GetOppsummeringsdokumentCommand(webClient, accessToken.getTokenValue(), organisajon.getOrgnummer(), kalendermaaned, miljo).call().block();
        if (dto == null) {
            return Optional.empty();
        }

        return Optional.of(new Opplysningspliktig(dto, organisajon.getDriverVirksomheter()));
    }

    public List<Opplysningspliktig> getAlleOpplysningspliktig(String miljo) {
        log.info("Henter alle opplysningspliktige fra {}...", miljo);
        AccessToken accessToken = tokenExchange.generateToken(properties).block();
        var list = new GetOppsummeringsdokumenterCommand(webClient, accessToken.getTokenValue(), miljo).call();
        log.info("Fant {} opplysningspliktig fra {}.", list.size(), miljo);
        return list.stream().map(value -> new Opplysningspliktig(value, new ArrayList<>())).collect(Collectors.toList());
    }

    @SneakyThrows
    public void sendOpplysningspliktig(Opplysningspliktig opplysningspliktig, String miljo) {
        saveOpplysningspliktig(opplysningspliktig, miljo).get();
    }

    @SneakyThrows
    public void sendOpplysningspliktig(List<Opplysningspliktig> opplysningspliktig, String miljo) {
        var futures = opplysningspliktig.stream().map(value -> saveOpplysningspliktig(value, miljo)).collect(Collectors.toList());
        for (var future : futures) {
            future.get();
        }
    }

}
