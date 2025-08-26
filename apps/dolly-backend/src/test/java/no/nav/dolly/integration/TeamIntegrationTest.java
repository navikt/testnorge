package no.nav.dolly.integration;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.entity.team.RsTeam;
import no.nav.dolly.domain.resultset.entity.team.RsTeamUpdate;
import no.nav.dolly.service.BrukerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.Mockito.when;

@Disabled
class TeamIntegrationTest extends AbstractIntegrasjonTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BrukerService brukerService;

    @BeforeEach
    void setUp() {

        var bruker = saveBruker(Bruker.builder()
                .brukerId(UUID.randomUUID().toString())
                .brukertype(Bruker.Brukertype.AZURE)
                .brukernavn("personlig bruker")
                .build()).block();

        var brukerForTeam = createTeamBruker()
                .block();

        var team = createTeam("Test Team", brukerForTeam, bruker)
                .block();

        createTeamBruker(bruker, team)
                .block();

        when(brukerService.fetchOrCreateBruker()).thenReturn(Mono.just(bruker));
        when(brukerService.fetchBrukerWithoutTeam()).thenReturn(Mono.just(bruker));
    }

    @Test
    void getAllTeams_shouldReturnListOfTeams() {

        webTestClient
                .get()
                .uri("/api/v1/team")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("1")
                .jsonPath("$[0].navn").isEqualTo("Test Team");
    }

    @Test
    void getTeamById_shouldReturnTeam() {

        webTestClient
                .get()
                .uri("/api/v1/team/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.navn").isEqualTo("Test Team");
    }

    @Test
    void createTeam_shouldReturnCreatedTeam() {

        webTestClient
                .post()
                .uri("/api/v1/team")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(RsTeam.builder()
                        .navn("Test Team 2")
                        .beskrivelse("Test Team Description")
                        .build())
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("$.navn").isEqualTo("Test Team 2")
                .jsonPath("$.beskrivelse").isEqualTo("Test Team Description");
    }

    @Test
    void updateTeam_shouldReturnUpdatedTeam() {

        webTestClient
                .put()
                .uri("/api/v1/team/1")
                .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(RsTeamUpdate.builder()
                                .navn("Test Team 2")
                                .beskrivelse("Test Team Description")
                                .build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.navn").isEqualTo("Test Team 2");
    }

    @Test
    void deleteTeam_shouldReturnNotFound() {

        webTestClient
                .delete()
                .uri("/api/v1/team/2")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void addTeamMember_shouldReturnCreated()  {

        var bruker2 = saveBruker(Bruker.builder()
                .brukerId(UUID.randomUUID().toString())
                .brukertype(Bruker.Brukertype.AZURE)
                .brukernavn("personlig bruker 2")
                .build()).block();

        webTestClient
                .post()
                .uri("/api/v1/team/1/medlem/" + bruker2.getBrukerId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    void removeTeamMember_shouldReturnNoContent() {

        webTestClient
                .delete()
                .uri("/api/v1/team/1/medlem/user1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNoContent();
    }
}