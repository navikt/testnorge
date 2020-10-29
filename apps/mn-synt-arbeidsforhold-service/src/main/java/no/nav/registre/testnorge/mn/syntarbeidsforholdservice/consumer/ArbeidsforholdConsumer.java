package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.libs.common.command.GetOpplysningspliktigCommand;
import no.nav.registre.testnorge.libs.common.command.SendOpplysningspliktigCommand;
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
            ClientCredentialGenerateAccessTokenService accessTokenService
    ) {
        this.arbeidsforholdApiClientProperties = arbeidsforholdApiClientProperties;
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient
                .builder()
                .baseUrl(arbeidsforholdApiClientProperties.getBaseUrl())
                .build();
    }


    public Opplysningspliktig getOpplysningspliktig(String orgnummer) {
        AccessToken accessToken = accessTokenService.generateToken(arbeidsforholdApiClientProperties);
        var dto = new GetOpplysningspliktigCommand(webClient, accessToken.getTokenValue(), orgnummer, "q2").call();
        return new Opplysningspliktig(dto);
    }

    public void sendOppsyninspliktig(Opplysningspliktig opplysningspliktig){
        AccessToken accessToken = accessTokenService.generateToken(arbeidsforholdApiClientProperties);
        new SendOpplysningspliktigCommand(webClient, accessToken.getTokenValue(), opplysningspliktig.toDTO(), "q2").run();
    }
}
