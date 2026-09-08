package no.nav.testnav.apps.tenorsearchservice;

import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.dolly.libs.test.DollyApplicationContextTest;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorMalBrukerType;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorPersonMal;
import no.nav.testnav.apps.tenorsearchservice.repository.TenorPersonMalRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DollySpringBootTest
class ApplicationContextTest extends DollyApplicationContextTest {

    @Autowired
    private TenorPersonMalRepository malRepository;

    @Test
    void shouldQueryH2TableUsingSpringDataIdentifiers() {
        var now = Instant.now();
        var mal = TenorPersonMal.builder()
                .malNavn("Min mal")
                .soekKriterier("{}")
                .brukerId("azure-id")
                .brukernavn("Testbruker")
                .brukertype(TenorMalBrukerType.AZURE)
                .opprettet(now)
                .sistOppdatert(now)
                .build();

        StepVerifier.create(malRepository.deleteAll()
                        .then(malRepository.save(mal))
                        .then(malRepository.findByBrukerIdAndMalNavnIgnoreCase(
                                "azure-id",
                                "MIN MAL")))
                .assertNext(savedMal -> assertThat(savedMal.getMalNavn()).isEqualTo("Min mal"))
                .verifyComplete();
    }

    @Test
    void shouldExposeTenorRequestSchemaForTemplateCriteria() {
        webTestClient.get()
                .uri("/v3/api-docs")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("""
                        $.components.schemas.OpprettTenorPersonMalRequest.properties.soekKriterier['$ref']
                        """)
                .isEqualTo("#/components/schemas/TenorRequest")
                .jsonPath("""
                        $.components.schemas.TenorPersonMalResponse.properties.soekKriterier['$ref']
                        """)
                .isEqualTo("#/components/schemas/TenorRequest")
                .jsonPath("$.components.schemas.TenorRequest.properties.arbeidsforhold")
                .exists()
                .jsonPath("$.components.schemas.TenorRequest.properties.beregnetSkatt")
                .exists()
                .jsonPath("$.components.schemas.TenorRequest.properties.tjenestepensjonsavtale")
                .exists()
                .jsonPath("$.components.schemas.TenorRequest.properties.roller.items.enum")
                .isArray();
    }
}
