package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.common.command.GetMNOrganisasjonerCommand;
import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.config.credentials.MNOrganisasjonApiServerProperties;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Organisajon;

@Component
public class MNOrganisasjonConsumer {

    private final WebClient webClient;
    private final NaisServerProperties properties;
    private final AccessTokenService accessTokenService;

    public MNOrganisasjonConsumer(
            MNOrganisasjonApiServerProperties properties,
            AccessTokenService accessTokenService
    ) {
        this.properties = properties;
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    @Cacheable("mini-norge-organiasjoner")
    public List<Organisajon> getOrganisajoner(String miljo) {
        AccessToken accessToken = accessTokenService.generateToken(properties);

        List<OrganisasjonDTO> list = new GetMNOrganisasjonerCommand(webClient, accessToken.getTokenValue(), miljo).call();
        return list.stream().map(Organisajon::new).collect(Collectors.toList());
    }
}