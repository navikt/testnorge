package no.nav.testnav.identpool.providers.v2;

import lombok.val;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.identpool.consumers.TpsMessagingConsumer;
import no.nav.testnav.identpool.domain.Ident2032;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.dto.IdentpoolRequestDTO;
import no.nav.testnav.identpool.dto.IdentpoolResponseDTO;
import no.nav.testnav.identpool.dto.TpsStatusDTO;
import no.nav.testnav.identpool.providers.v1.AbstractTestcontainer;
import no.nav.testnav.identpool.repository.PersonidentifikatorRepository;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

@DollySpringBootTest
class IdentControllerTest extends AbstractTestcontainer {

    private static final String IDENT_V2_BASEURL = "/api/v2/ident";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private PersonidentifikatorRepository personidentifikatorRepository;

    @MockitoBean
    private TpsMessagingConsumer tpsMessagingConsumer;


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {

        personidentifikatorRepository.deleteAll().block();
    }

    @Test
    void rekvirer() {

        val dato = LocalDate.of(2000, 1, 1);

        val request = new IdentpoolRequestDTO(Identtype.FNR, dato, dato);

        val response = webTestClient.post()
                .uri(IDENT_V2_BASEURL + "/rekvirer")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(IdentpoolResponseDTO.class)
                .returnResult()
                .getResponseBody();

        assertThat(response.foedselsdato(), is(equalTo(dato)));
        assertThat(response.personidentifikator(), CoreMatchers.startsWith("014100999"));

        StepVerifier
                .create(personidentifikatorRepository.findAvail("014100")
                        .collectList())
                .expectNextMatches(idents -> idents.size() == 3)
                .verifyComplete();
    }

    @Disabled
    @Test
    void validerProduksjonIdenter() {

        val ident = "01010112345";
        when(tpsMessagingConsumer.getIdenterProdStatus(anySet())).thenReturn(Flux.just(TpsStatusDTO.builder()
                        .ident(ident)
                        .inUse(true)
                        .build()));

        val test = webTestClient.post()
                .uri(IDENT_V2_BASEURL + "/validerflere")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"identer\":\"" + ident + "\"}")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(IdentpoolResponseDTO[].class)
                .returnResult();
//                .toString();
//                .jsonPath("$.ident").isEqualTo("01010112345")
//                .jsonPath("$.gyldig").isEqualTo(true);
    }

    @Test
    void valider() {
    }

    @Test
    void frigjoer() {

        val ident = "01010112345";
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
                .expectStatus()
                .isOk();

        StepVerifier.create(personidentifikatorRepository.findByPersonidentifikator(ident))
                .expectNextMatches(ident2032 -> !ident2032.isAllokert())
                .verifyComplete();
    }
}