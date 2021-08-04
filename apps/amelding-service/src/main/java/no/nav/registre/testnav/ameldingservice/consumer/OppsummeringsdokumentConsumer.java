package no.nav.registre.testnav.ameldingservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.Optional;

import no.nav.registre.testnav.ameldingservice.credentials.OppsummeringsdokumentServerProperties;
import no.nav.registre.testnorge.libs.common.command.GetOppsummeringsdokumentCommand;
import no.nav.registre.testnorge.libs.common.command.GetOppsummeringsdokumentByIdCommand;
import no.nav.registre.testnorge.libs.common.command.SaveOppsummeringsdokumenterCommand;
import no.nav.testnav.libs.servletcore.config.ApplicationProperties;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.OppsummeringsdokumentDTO;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.Populasjon;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Component
public class OppsummeringsdokumentConsumer {
    private static final int BYTE_COUNT = 16 * 1024 * 1024;
    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final NaisServerProperties properties;
    private final ApplicationProperties applicationProperties;

    public OppsummeringsdokumentConsumer(
            AccessTokenService accessTokenService,
            OppsummeringsdokumentServerProperties properties,
            ObjectMapper objectMapper,
            ApplicationProperties applicationProperties
    ) {
        this.applicationProperties = applicationProperties;
        this.accessTokenService = accessTokenService;
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

    public String saveOpplysningspliktig(OppsummeringsdokumentDTO dto, String miljo) {
        AccessToken accessToken = accessTokenService.generateToken(properties).block();
        return new SaveOppsummeringsdokumenterCommand(
                webClient,
                accessToken.getTokenValue(),
                dto,
                miljo,
                applicationProperties.getName(),
                Populasjon.DOLLY
        ).call();
    }


    public Optional<OppsummeringsdokumentDTO> get(String opplysningsplikitgOrgnummer, LocalDate kalendermaaned, String miljo) {
        AccessToken accessToken = accessTokenService.generateToken(properties).block();
        var dto = new GetOppsummeringsdokumentCommand(
                webClient,
                accessToken.getTokenValue(),
                opplysningsplikitgOrgnummer,
                kalendermaaned,
                miljo
        ).call();
        return Optional.ofNullable(dto);
    }

    public Optional<OppsummeringsdokumentDTO> get(String id) {
        AccessToken accessToken = accessTokenService.generateToken(properties).block();
        var dto = new GetOppsummeringsdokumentByIdCommand(webClient, accessToken.getTokenValue(), id).call();
        return Optional.ofNullable(dto);
    }

}
