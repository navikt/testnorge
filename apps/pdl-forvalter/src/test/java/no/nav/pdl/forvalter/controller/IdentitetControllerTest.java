package no.nav.pdl.forvalter.controller;

import no.nav.pdl.forvalter.service.IdentitetService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonIDDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(IdentitetController.class)
class IdentitetControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private IdentitetService identitetService;

    @Test
    void shouldSearchIdentiteterReactively() {

        var personID = PersonIDDTO.builder()
                .ident("12345678901")
                .fornavn("Ola")
                .etternavn("Nordmann")
                .build();

        when(identitetService.getfragment(eq("Ola"), any()))
                .thenReturn(List.of(personID));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/identiteter")
                        .queryParam("fragment", "Ola")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.valueOf("application/json;charset=utf-8"))
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[0].ident").isEqualTo("12345678901")
                .jsonPath("$[0].fornavn").isEqualTo("Ola")
                .jsonPath("$[0].etternavn").isEqualTo("Nordmann");
    }

    @Test
    void shouldUpdateStandaloneReactively() {

        doNothing().when(identitetService).updateStandalone("12345678901", true);

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt())
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri("/api/v1/identiteter/12345678901/standalone/true")
                .exchange()
                .expectStatus().isOk();

        verify(identitetService).updateStandalone("12345678901", true);
    }

}
