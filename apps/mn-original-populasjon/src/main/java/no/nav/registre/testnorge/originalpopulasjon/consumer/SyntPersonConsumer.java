package no.nav.registre.testnorge.originalpopulasjon.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.syntperson.v1.SyntPersonDTO;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import no.nav.registre.testnorge.originalpopulasjon.config.credentials.SyntPersonServiceProperties;
import no.nav.registre.testnorge.originalpopulasjon.exceptions.SyntetiseringAvPersonInfoException;

@Slf4j
@Component
@DependencyOn("synt-person-api")
public class SyntPersonConsumer {

    private final WebClient webClient;
    private final SyntPersonServiceProperties serviceProperties;
    private final AccessTokenService accessTokenService;

    public SyntPersonConsumer(
            SyntPersonServiceProperties serviceProperties,
            AccessTokenService accessTokenService
    ) {
        this.serviceProperties = serviceProperties;
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient
                .builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }


    public List<SyntPersonDTO> getPersonInfo(Integer antall) {
        AccessToken accessToken = accessTokenService.generateToken(serviceProperties);

        log.info("Henter personopplysninger for {} personer", antall);
        var response = webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/synt-person").queryParam("antall", antall).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getTokenValue())
                .retrieve()
                .bodyToMono(SyntPersonDTO[].class)
                .block();


        if (response == null) {
            throw new SyntetiseringAvPersonInfoException("Klarte ikke hente personinfo fra synt-person-api");
        }

        var personListe = Arrays.asList(response);
        log.info("Hentet personinfo for {} av {} personer", personListe.size(), antall);
        return personListe;
    }
}
