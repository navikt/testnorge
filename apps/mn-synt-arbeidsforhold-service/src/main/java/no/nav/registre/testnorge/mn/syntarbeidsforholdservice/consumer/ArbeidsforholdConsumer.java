package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.Optional;

import no.nav.registre.testnorge.libs.common.command.GetOppsummeringsdokumentetCommand;
import no.nav.registre.testnorge.libs.common.command.SaveOpplysningspliktigCommand;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.ClientCredentialGenerateAccessTokenService;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.credentials.ArbeidsforholdApiClientProperties;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Opplysningspliktig;

@Component
public class ArbeidsforholdConsumer {
    private final WebClient webClient;
    private final ArbeidsforholdApiClientProperties arbeidsforholdApiClientProperties;
    private final ClientCredentialGenerateAccessTokenService accessTokenService;

    public ArbeidsforholdConsumer(
            ArbeidsforholdApiClientProperties arbeidsforholdApiClientProperties,
            ClientCredentialGenerateAccessTokenService accessTokenService,
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
        AccessToken accessToken = accessTokenService.generateToken(arbeidsforholdApiClientProperties);
        var dto = new GetOppsummeringsdokumentetCommand(webClient, accessToken.getTokenValue(), orgnummer, kalendermaaned, miljo).call();
        if(dto == null){
            return Optional.empty();
        }

        return Optional.of(new Opplysningspliktig(dto));
    }

    public void sendOpplysningspliktig(Opplysningspliktig opplysningspliktig, String miljo) {
        AccessToken accessToken = accessTokenService.generateToken(arbeidsforholdApiClientProperties);
        new SaveOpplysningspliktigCommand(webClient, accessToken.getTokenValue(), opplysningspliktig.toDTO(), miljo).run();
    }
}
