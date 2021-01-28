package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
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

import no.nav.registre.testnorge.libs.common.command.GetOppsummeringsdokumenterCommand;
import no.nav.registre.testnorge.libs.common.command.GetOppsummeringsdokumentetCommand;
import no.nav.registre.testnorge.libs.common.command.SaveOppsummeringsdokumenterCommand;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.config.credentials.ArbeidsforholdApiServerProperties;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Opplysningspliktig;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Organisajon;

@Component
public class ArbeidsforholdConsumer {
    private static final int BYTE_COUNT = 16 * 1024 * 1024;
    private final WebClient webClient;
    private final NaisServerProperties properties;
    private final AccessTokenService accessTokenService;
    private final Executor executor;

    public ArbeidsforholdConsumer(
            ArbeidsforholdApiServerProperties properties,
            AccessTokenService accessTokenService,
            ObjectMapper objectMapper
    ) {
        this.properties = properties;
        this.accessTokenService = accessTokenService;
        this.executor = Executors.newFixedThreadPool(5);
        this.webClient = WebClient
                .builder()
                .codecs(clientDefaultCodecsConfigurer -> {
                    clientDefaultCodecsConfigurer.defaultCodecs().maxInMemorySize(BYTE_COUNT);
                    clientDefaultCodecsConfigurer
                            .defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    clientDefaultCodecsConfigurer
                            .defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                })
                .baseUrl(properties.getUrl())
                .build();
    }


    private CompletableFuture<Void> saveOpplysningspliktig(Opplysningspliktig opplysningspliktig, String miljo) {
        AccessToken accessToken = accessTokenService.generateToken(properties);
        return CompletableFuture.supplyAsync(
                () -> new SaveOppsummeringsdokumenterCommand(
                        webClient,
                        accessToken.getTokenValue(),
                        opplysningspliktig.toDTO(),
                        miljo
                ).call(),
                executor
        );
    }

    public Optional<Opplysningspliktig> getOpplysningspliktig(Organisajon organisajon, LocalDate kalendermaaned, String miljo) {
        AccessToken accessToken = accessTokenService.generateToken(properties);
        var dto = new GetOppsummeringsdokumentetCommand(webClient, accessToken.getTokenValue(), organisajon.getOrgnummer(), kalendermaaned, miljo).call();
        if (dto == null) {
            return Optional.empty();
        }

        return Optional.of(new Opplysningspliktig(dto, organisajon.getDriverVirksomheter()));
    }

    public List<Opplysningspliktig> getAlleOpplysningspliktig(String miljo) {
        AccessToken accessToken = accessTokenService.generateToken(properties);
        var list = new GetOppsummeringsdokumenterCommand(webClient, accessToken.getTokenValue(), miljo).call();
        //TODO: Fix empty array of driver virksomheter
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
