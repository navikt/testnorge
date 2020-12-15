package no.nav.registre.orgnrservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.libs.common.command.GetOrganisasjonCommand;
import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Slf4j
@Component
public class OrganisasjonApiConsumer {

    private WebClient webClient;
    private final String clientId;
    private final AccessTokenService accessTokenService;

    OrganisasjonApiConsumer (@Value("${consumers.organisasjon-api.url}") String url,
                             @Value("${consumers.organisasjon-api.client_id}") String clientId,
                             AccessTokenService accessTokenService) {
        this.clientId = clientId;
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient.builder()
                .baseUrl(url)
                .build();
    }

    OrganisasjonDTO getOrgnrFraMiljoe (String orgnummer, String miljoe, String token) {
        try {
            OrganisasjonDTO call = new GetOrganisasjonCommand(webClient, token, orgnummer, miljoe).call();
            log.info("Fikk kontakt med EREG og sjekka Q1");
            return call;
        } catch( Exception e) {
            throw new RuntimeException("Kunne ikke hente organisasjon " + orgnummer + " fra miljoe " + miljoe);
        }
    }

    public OrganisasjonDTO getOrgnr (String orgnummer) {
        //evt loop gjennom miljø. Lage tråder
        String miljoe = "q1";
        String token = accessTokenService.generateToken(clientId).getTokenValue();
        return getOrgnrFraMiljoe(orgnummer, miljoe, token);
    }
}
