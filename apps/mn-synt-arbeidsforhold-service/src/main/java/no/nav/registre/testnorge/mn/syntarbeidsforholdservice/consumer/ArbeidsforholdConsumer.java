package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.common.command.GetOppsummeringsdokumenterCommand;
import no.nav.registre.testnorge.libs.common.command.GetOppsummeringsdokumentetCommand;
import no.nav.registre.testnorge.libs.common.command.SaveOppsummeringsdokumenterCommand;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.credentials.ArbeidsforholdApiClientProperties;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Opplysningspliktig;

@Component
public class ArbeidsforholdConsumer {
    private final WebClient webClient;
    private final ArbeidsforholdApiClientProperties arbeidsforholdApiClientProperties;
    private final AccessTokenService accessTokenService;

    public ArbeidsforholdConsumer(
            ArbeidsforholdApiClientProperties arbeidsforholdApiClientProperties,
            AccessTokenService accessTokenService,
            ObjectMapper objectMapper
    ) {
        this.arbeidsforholdApiClientProperties = arbeidsforholdApiClientProperties;
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient
                .builder()
                .codecs(clientDefaultCodecsConfigurer -> {
                    clientDefaultCodecsConfigurer
                            .defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    clientDefaultCodecsConfigurer
                            .defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                })
                .baseUrl(arbeidsforholdApiClientProperties.getBaseUrl())
                .build();
    }


    public Optional<Opplysningspliktig> getOpplysningspliktig(String orgnummer, LocalDate kalendermaaned, String miljo) {
        AccessToken accessToken = accessTokenService.generateToken(arbeidsforholdApiClientProperties.getClientId());
        var dto = new GetOppsummeringsdokumentetCommand(webClient, accessToken.getTokenValue(), orgnummer, kalendermaaned, miljo).call();
        if (dto == null) {
            return Optional.empty();
        }

        return Optional.of(new Opplysningspliktig(dto));
    }

    public List<Opplysningspliktig> getAlleOpplysningspliktig(String miljo) {
        AccessToken accessToken = accessTokenService.generateToken(arbeidsforholdApiClientProperties.getClientId());
        var list = new GetOppsummeringsdokumenterCommand(webClient, accessToken.getTokenValue(), miljo).call();

        return list.stream().map(Opplysningspliktig::new).collect(Collectors.toList());
    }

    public void sendOpplysningspliktig(Opplysningspliktig opplysningspliktig, String miljo) {
        AccessToken accessToken = accessTokenService.generateToken(arbeidsforholdApiClientProperties.getClientId());
        new SaveOppsummeringsdokumenterCommand(webClient, accessToken.getTokenValue(), opplysningspliktig.toDTO(), miljo).run();
    }
}
