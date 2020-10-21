package no.nav.registre.testnorge.originalpopulasjon.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.syntperson.v1.SyntPersonDTO;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.domain.ClientCredential;
import no.nav.registre.testnorge.libs.oauth2.service.ClientCredentialGenerateAccessTokenService;
import no.nav.registre.testnorge.originalpopulasjon.credentials.SyntPersonApiClientCredential;
import no.nav.registre.testnorge.originalpopulasjon.exceptions.SyntetiseringAvPersonInfoException;

@Slf4j
@Component
@DependencyOn("synt-person-api")
public class SyntPersonConsumer {

    private final WebClient webClient;
    private final ClientCredential clientCredential;
    private final ClientCredentialGenerateAccessTokenService accessTokenService;

    public SyntPersonConsumer(
            @Value("${consumer.synt-person-api.url}") String url,
            SyntPersonApiClientCredential clientCredential,
            ClientCredentialGenerateAccessTokenService accessTokenService
    ) {
        this.clientCredential = clientCredential;
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient
                .builder()
                .baseUrl(url)
                .build();
    }


    public List<SyntPersonDTO> getPersonInfo(Integer antall) {
        AccessToken accessToken = accessTokenService.generateToken(
                clientCredential,
                new AccessScopes("api://" + clientCredential.getClientId() + "/.default")
        );

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
