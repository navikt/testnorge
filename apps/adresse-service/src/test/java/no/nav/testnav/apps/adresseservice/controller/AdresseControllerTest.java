package no.nav.testnav.apps.adresseservice.controller;

import no.nav.testnav.apps.adresseservice.dto.MatrikkeladresseRequest;
import no.nav.testnav.apps.adresseservice.dto.VegadresseRequest;
import no.nav.testnav.apps.adresseservice.service.PdlAdresseService;
import no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = AdresseController.class)
@Import(AdresseControllerTest.TestSecurityConfig.class)
class AdresseControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private PdlAdresseService pdlAdresseService;

    private <T> void assertResponseList(List<T> value) {
        assertThat(value).isNotNull();
    }

    static class TestSecurityConfig {

        @Bean
        SecurityWebFilterChain testSecurityWebFilterChain(ServerHttpSecurity httpSecurity) {
            return httpSecurity
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(exchange -> exchange.anyExchange().permitAll())
                    .build();
        }

        @Bean
        PdlAdresseService pdlAdresseService() {
            return Mockito.mock(PdlAdresseService.class);
        }
    }

    @Test
    void shouldGetVegadresse() {
        when(pdlAdresseService.getVegadresse(any(VegadresseRequest.class), anyLong()))
                .thenReturn(Mono.just(List.of(VegadresseDTO.builder().build())));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/adresser/veg")
                        .queryParam("postnummer", "0557")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(VegadresseDTO.class)
                .value(this::assertResponseList);
    }

    @Test
    void shouldGetMatrikkeladresse() {
        when(pdlAdresseService.getMatrikkelAdresse(any(MatrikkeladresseRequest.class), anyLong()))
                .thenReturn(Mono.just(List.of(MatrikkeladresseDTO.builder().build())));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/adresser/matrikkeladresse")
                        .queryParam("kommunenummer", "0301")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MatrikkeladresseDTO.class)
                .value(this::assertResponseList);
    }
}
