package no.nav.testnav.apps.brukerservice.integrationtest;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.testnav.apps.brukerservice.dto.BrukerDTO;
import no.nav.testnav.integrationtest.client.TokendingsClient;
import no.nav.testnav.libs.securitycore.config.UserConstant;

@Tag("integration")
class BrukerServiceIntegrationTest {

    private static final String PID = "01810048413";
    private static final String ORGNUMMER = "811306312";
    private final TokendingsClient tokendingsClient = new TokendingsClient();
    private final WebClient webClient = WebClient.builder().baseUrl("http://localhost:8002").build();

    @Test
    void should_create_new_user_login_change_username_and_then_delete_user() {

        var token = tokendingsClient.generateToken("dev-gcp:dolly:testnav-bruker-service", PID).block();

        // Create user
        var expected = new BrukerDTO(null, "username", ORGNUMMER, null, null);
        var bruker = webClient.post()
                .uri("/api/v1/brukere")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getTokenValue())
                .body(BodyInserters.fromValue(expected))
                .retrieve()
                .bodyToMono(BrukerDTO.class)
                .block();

        Assertions.assertThat(bruker)
                .usingRecursiveComparison()
                .comparingOnlyFields("brukernavn", "organisasjonsnummer")
                .isEqualTo(expected);


        // Login user
        var userJwt = webClient.post()
                .uri(builder -> builder.path("/api/v1/brukere/{id}/token").build(bruker.id()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getTokenValue())
                .retrieve()
                .bodyToMono(String.class)
                .block();


        // Change username
        webClient.patch()
                .uri(builder -> builder.path("/api/v1/brukere/{id}/brukernavn").build(bruker.id()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getTokenValue())
                .header(UserConstant.USER_HEADER_JWT, userJwt)
                .body(BodyInserters.fromValue(
                        "new-username"
                ))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Get updated user
        var updatedUser = webClient.get()
                .uri(builder -> builder.path("/api/v1/brukere/{id}").build(bruker.id()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getTokenValue())
                .header(UserConstant.USER_HEADER_JWT, userJwt)
                .retrieve()
                .bodyToMono(BrukerDTO.class)
                .block();

        Assertions.assertThat(updatedUser.brukernavn()).isEqualTo("new-username");

        // Delete user
        webClient.delete()
                .uri(builder -> builder.path("/api/v1/brukere/{id}").build(bruker.id()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getTokenValue())
                .header(UserConstant.USER_HEADER_JWT, userJwt)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }


}
