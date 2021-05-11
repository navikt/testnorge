package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command.GetOpplysningspliktigOrgnummerCommand;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.credentials.GenererOrganisasjonPopulasjonServerProperties;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
@Component
public class GenererOrganisasjonPopulasjonConsumer {
    private final WebClient webClient;
    private final NaisServerProperties properties;
    private final AccessTokenService accessTokenService;

    public GenererOrganisasjonPopulasjonConsumer(
            GenererOrganisasjonPopulasjonServerProperties properties,
            AccessTokenService accessTokenService
    ) {
        this.properties = properties;
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public Set<String> getOpplysningspliktig(String miljo) {
        var accessToken = accessTokenService.generateToken(properties);
        return new GetOpplysningspliktigOrgnummerCommand(webClient, accessToken.getTokenValue(), miljo).call();
    }
}
