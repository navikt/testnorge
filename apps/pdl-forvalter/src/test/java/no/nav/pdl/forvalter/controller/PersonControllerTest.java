package no.nav.pdl.forvalter.controller;

import no.nav.pdl.forvalter.service.ArtifactDeleteService;
import no.nav.pdl.forvalter.service.ArtifactUpdateService;
import no.nav.pdl.forvalter.service.MetadataTidspunkterService;
import no.nav.pdl.forvalter.service.PdlOrdreService;
import no.nav.pdl.forvalter.service.PersonService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@WebFluxTest(PersonController.class)
class PersonControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private PersonService personService;

    @MockitoBean
    private PdlOrdreService pdlOrdreService;

    @MockitoBean
    private ArtifactDeleteService artifactDeleteService;

    @MockitoBean
    private ArtifactUpdateService artifactUpdateService;

    @MockitoBean
    private MetadataTidspunkterService metadataTidspunkterService;

    @Test
    void shouldReturnPersonerReactively() {

        var fullPersonDTO = FullPersonDTO.builder()
                .person(PersonDTO.builder().ident("12345678901").build())
                .build();

        when(personService.getPerson(any(), any()))
                .thenReturn(List.of(fullPersonDTO));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/personer")
                        .queryParam("identer", "12345678901")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.valueOf("application/json;charset=utf-8"))
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[0].person.ident").isEqualTo("12345678901");
    }

    @Test
    void shouldReturnEmptyListWhenNoPersoner() {

        when(personService.getPerson(any(), any()))
                .thenReturn(Collections.emptyList());

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt())
                .get()
                .uri("/api/v1/personer")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    void shouldDeletePersonReactively() {

        doNothing().when(personService).deletePerson(any());

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt())
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .delete()
                .uri("/api/v1/personer/12345678901")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldDeleteArtifactReactively() {

        doNothing().when(artifactDeleteService).deleteNavn(any(), any());

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt())
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .delete()
                .uri("/api/v1/personer/12345678901/navn/1")
                .exchange()
                .expectStatus().isOk();
    }

}
