package no.nav.testnav.apps.persontilgangservice.integrationtest;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.testnav.apps.persontilgangservice.controller.dto.OrganisasjonDTO;
import no.nav.testnav.integrationtest.client.TokendingsClient;

@Tag("integration")
class PersonTilgangServiceIntegrationTest {

    private static final String PID = "01810048413";
    private final TokendingsClient tokendingsClient = new TokendingsClient();
    private final WebClient webClient = WebClient.builder().baseUrl("http://localhost:8001").build();

    @Test
    void should_get_all_organisasjons_user_has_access_to_and_get_a_single_organisasjon() {

        // Opprett token for brukeren
        var token = tokendingsClient.generateToken("dev-gcp:dolly:testnav-person-organisasjon-tilgang-service", PID).block();

        // Hent alle organiasjoner brukeren har tilgang til
        var organisasjoner = webClient.get()
                .uri("/api/v1/person/organisasjoner")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getTokenValue())
                .retrieve()
                .bodyToMono(OrganisasjonDTO[].class)
                .block();

        Assertions.assertThat(organisasjoner).isNotEmpty();

        // Hent en organisasjon
        var organisasjon = webClient.get()
                .uri(builder -> builder.path("/api/v1/person/organisasjoner/{organisasjonsnummer}").build(organisasjoner[0].organisasjonsnummer()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getTokenValue())
                .retrieve()
                .bodyToMono(OrganisasjonDTO.class)
                .block();

        Assertions.assertThat(organisasjon).isNotNull();
    }


}
