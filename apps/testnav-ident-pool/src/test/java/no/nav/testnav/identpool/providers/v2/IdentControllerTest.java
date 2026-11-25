package no.nav.testnav.identpool.providers.v2;

import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.identpool.config.AbstractTestcontainer;
import no.nav.testnav.identpool.domain.Ident2032;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.dto.IdentpoolRequestDTO;
import no.nav.testnav.identpool.repository.PersonidentifikatorRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.time.LocalDate;

@DollySpringBootTest
class IdentControllerTest extends AbstractTestcontainer {

    private static final String IDENT_V2_BASEURL = "/api/v2/ident";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private PersonidentifikatorRepository personidentifikatorRepository;

    @AfterEach
    void tearDown() {
        personidentifikatorRepository.deleteAll().block();
    }

    @Test
    void shouldRequisitionIdentForRequestedBirthDate() {
        var date = LocalDate.of(2000, 1, 1);
        var request = new IdentpoolRequestDTO(Identtype.FNR, date, date);

        webTestClient.post()
                .uri(IDENT_V2_BASEURL + "/rekvirer")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.personidentifikator").isNotEmpty()
                .jsonPath("$.foedselsdato").isEqualTo(date.toString());

        StepVerifier.create(personidentifikatorRepository.findAvail("014100").collectList())
                .expectNextMatches(idents -> idents.size() == 3)
                .verifyComplete();
    }

    @ParameterizedTest
    @CsvSource({"1, 0", "0, 1", "0, -1"})
    void shouldRejectInvalidBirthDateBounds(int startOffset, int endOffset) {
        var request = new IdentpoolRequestDTO(Identtype.FNR,
                LocalDate.now().plusDays(startOffset), LocalDate.now().plusDays(endOffset));

        webTestClient.post()
                .uri(IDENT_V2_BASEURL + "/rekvirer")
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReleaseAllocatedIdent() {
        var ident = "01010112345";
        personidentifikatorRepository.save(Ident2032.builder()
                        .identtype(Identtype.FNR)
                        .personidentifikator(ident)
                        .datoIdentifikator("010101")
                        .individnummer(123)
                        .allokert(true)
                        .foedselsdato(LocalDate.of(2001, 1, 1))
                        .datoAllokert(LocalDate.now())
                        .build())
                .block();

        StepVerifier.create(personidentifikatorRepository.findByPersonidentifikator(ident))
                .expectNextMatches(Ident2032::isAllokert)
                .verifyComplete();

        webTestClient.delete()
                .uri(IDENT_V2_BASEURL + "/frigjoer/{ident}", ident)
                .exchange()
                .expectStatus().isOk();

        StepVerifier.create(personidentifikatorRepository.findByPersonidentifikator(ident))
                .expectNextMatches(ident2032 -> !ident2032.isAllokert())
                .verifyComplete();
    }
}
