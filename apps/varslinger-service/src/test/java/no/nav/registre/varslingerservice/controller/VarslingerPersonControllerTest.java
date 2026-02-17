package no.nav.registre.varslingerservice.controller;

import no.nav.registre.varslingerservice.SecurityTestConfig;
import no.nav.registre.varslingerservice.repository.BrukerRepository;
import no.nav.registre.varslingerservice.repository.MottattVarslingRepository;
import no.nav.registre.varslingerservice.repository.VarslingRepository;
import no.nav.registre.varslingerservice.repository.model.BrukerModel;
import no.nav.registre.varslingerservice.repository.model.MottattVarslingModel;
import no.nav.registre.varslingerservice.repository.model.VarslingModel;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.libs.securitycore.domain.Token;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@DollySpringBootTest
@Import(SecurityTestConfig.class)
class VarslingerPersonControllerTest {

    @MockitoBean
    public GetAuthenticatedToken getAuthenticatedToken;

    @MockitoBean
    public GetAuthenticatedUserId getAuthenticatedUserId;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private VarslingRepository varslingRepository;

    @Autowired
    private MottattVarslingRepository mottattVarslingRepository;

    @Autowired
    private BrukerRepository brukerRepository;

    @AfterEach
    void afterEach() {
        mottattVarslingRepository.deleteAll();
        brukerRepository.deleteAll();
        varslingRepository.deleteAll();
    }

    @Test
    void testNoWarningsInRepository() {
        var loggedInUser = brukerRepository.save(BrukerModel.builder().objectId("bruker1").build());

        when(getAuthenticatedToken.call())
                .thenReturn(Mono.just(Token.builder().clientCredentials(false).build()));
        when(getAuthenticatedUserId.call())
                .thenReturn(Mono.just(loggedInUser.getObjectId()));

        webTestClient.get().uri("/api/v1/varslinger/person/ids")
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("[]");
    }

    @Test
    void testTwoOfThreeWarningsInRepositoryBelongToLoggedInUser() {
        var v1 = varslingRepository.save(VarslingModel.builder().varslingId("varsel1").build());
        var v2 = varslingRepository.save(VarslingModel.builder().varslingId("varsel2").build());
        var v3 = varslingRepository.save(VarslingModel.builder().varslingId("varsel3").build());
        var loggedInUser = brukerRepository.save(BrukerModel.builder().objectId("bruker1").build());
        var otherUser = brukerRepository.save(BrukerModel.builder().objectId("bruker2").build());
        mottattVarslingRepository.save(MottattVarslingModel.builder().varsling(v1).bruker(loggedInUser).build());
        mottattVarslingRepository.save(MottattVarslingModel.builder().varsling(v2).bruker(otherUser).build());
        mottattVarslingRepository.save(MottattVarslingModel.builder().varsling(v3).bruker(loggedInUser).build());

        when(getAuthenticatedToken.call())
                .thenReturn(Mono.just(Token.builder().clientCredentials(false).build()));
        when(getAuthenticatedUserId.call())
                .thenReturn(Mono.just(loggedInUser.getObjectId()));

        webTestClient.get().uri("/api/v1/varslinger/person/ids")
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("[\"varsel1\",\"varsel3\"]");
    }

    @Test
    void testUpdateWarning() {
        var v1 = varslingRepository.save(VarslingModel.builder().varslingId("varsel1").build());
        var loggedInUser = brukerRepository.save(BrukerModel.builder().objectId("bruker1").build());

        when(getAuthenticatedToken.call())
                .thenReturn(Mono.just(Token.builder().clientCredentials(false).build()));
        when(getAuthenticatedUserId.call())
                .thenReturn(Mono.just(loggedInUser.getObjectId()));

        webTestClient.put().uri("/api/v1/varslinger/person/ids/{varslingId}", v1.getVarslingId())
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Location", "/api/v1/varslinger/person/ids/" + v1.getVarslingId());
    }

    @Test
    void testGetNonexistingWarning() {
        var loggedInUser = brukerRepository.save(BrukerModel.builder().objectId("bruker1").build());

        when(getAuthenticatedToken.call())
                .thenReturn(Mono.just(Token.builder().clientCredentials(false).build()));
        when(getAuthenticatedUserId.call())
                .thenReturn(Mono.just(loggedInUser.getObjectId()));

        webTestClient.get().uri("/api/v1/varslinger/person/ids/{varslingId}", "someNonExistingId")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testGetSingleWarning() {
        var v1 = varslingRepository.save(VarslingModel.builder().varslingId("varsel1").build());
        var loggedInUser = brukerRepository.save(BrukerModel.builder().objectId("bruker1").build());
        mottattVarslingRepository.save(MottattVarslingModel.builder().varsling(v1).bruker(loggedInUser).build());

        when(getAuthenticatedToken.call())
                .thenReturn(Mono.just(Token.builder().clientCredentials(false).build()));
        when(getAuthenticatedUserId.call())
                .thenReturn(Mono.just(loggedInUser.getObjectId()));

        webTestClient.get().uri("/api/v1/varslinger/person/ids/{varslingId}", v1.getVarslingId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo(v1.getVarslingId());
    }

    @Test
    void testDeleteNonExistingWarning() {
        var loggedInUser = brukerRepository.save(BrukerModel.builder().objectId("bruker1").build());

        when(getAuthenticatedToken.call())
                .thenReturn(Mono.just(Token.builder().clientCredentials(false).build()));
        when(getAuthenticatedUserId.call())
                .thenReturn(Mono.just(loggedInUser.getObjectId()));

        webTestClient.delete().uri("/api/v1/varslinger/person/ids/{varslingId}", "someNonExistingId")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testDeleteWarning() {
        var v1 = varslingRepository.save(VarslingModel.builder().varslingId("varsel1").build());
        var v2 = varslingRepository.save(VarslingModel.builder().varslingId("varsel2").build());
        var loggedInUser = brukerRepository.save(BrukerModel.builder().objectId("bruker1").build());
        var otherUser = brukerRepository.save(BrukerModel.builder().objectId("bruker2").build());
        var mv1 = mottattVarslingRepository.save(MottattVarslingModel.builder().varsling(v1).bruker(loggedInUser).build());
        var mv2 = mottattVarslingRepository.save(MottattVarslingModel.builder().varsling(v2).bruker(otherUser).build());

        when(getAuthenticatedToken.call())
                .thenReturn(Mono.just(Token.builder().clientCredentials(false).build()));
        when(getAuthenticatedUserId.call())
                .thenReturn(Mono.just(loggedInUser.getObjectId()));

        webTestClient.delete().uri("/api/v1/varslinger/person/ids/{varslingId}", v1.getVarslingId())
                .exchange()
                .expectStatus().isOk();

        assertThat(mottattVarslingRepository.findById(mv1.getId()))
                .isEmpty();
        assertThat(mottattVarslingRepository.findById(mv2.getId()))
                .isNotEmpty();
        assertThat(varslingRepository.findById(v1.getVarslingId()))
                .isNotEmpty();
        assertThat(varslingRepository.findById(v2.getVarslingId()))
                .isNotEmpty();

    }

}
