package no.nav.registre.testnorge.profil.integrationtest;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.testnav.integrationtest.client.AzureAdClient;
import no.nav.testnav.libs.dto.profil.v1.ProfilDTO;

@Tag("integration")
class ProfilIntegrationTest {

    private final AzureAdClient azureAdClient = new AzureAdClient();
    private final WebClient webClient = WebClient.builder().baseUrl("http://localhost:8003").build();

    @Test
    void should_get_azure_profil() {
        var token = azureAdClient.generateToken("dev-gcp.dolly.testnorge-profil-api", "sad", "dsa").block();

        var actual = webClient.get()
                .uri("/api/v1/profil")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getTokenValue())
                .retrieve()
                .bodyToMono(ProfilDTO.class)
                .block();

        Assertions
                .assertThat(actual)
                .isEqualTo(new ProfilDTO("Ola Normand", "dolly@nav.no", "NAV", "NAV", "AzureAD"));

    }

}