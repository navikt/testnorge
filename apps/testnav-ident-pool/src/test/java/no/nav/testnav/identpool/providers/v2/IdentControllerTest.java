package no.nav.testnav.identpool.providers.v2;

import lombok.val;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.identpool.config.AbstractTestcontainer;
import no.nav.testnav.identpool.consumers.TpsMessagingConsumer;
import no.nav.testnav.identpool.domain.Ident2032;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Kjoenn;
import no.nav.testnav.identpool.dto.IdentpoolRequestDTO;
import no.nav.testnav.identpool.dto.TpsStatusDTO;
import no.nav.testnav.identpool.repository.PersonidentifikatorRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;

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

    @AfterEach
    void tearDown() {

        personidentifikatorRepository.deleteAll().block();
    }

    @Test
    void rekvirer() {

        val dato = LocalDate.of(2000, 1, 1);

        val request = new IdentpoolRequestDTO(Identtype.FNR, dato, dato);

        webTestClient.post()
                .uri(IDENT_V2_BASEURL + "/rekvirer")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.personidentifikator").isNotEmpty()
                .jsonPath("$.foedselsdato").isEqualTo(dato.toString());

        StepVerifier
                .create(personidentifikatorRepository.findAvail("014100")
                        .collectList())
                .expectNextMatches(idents -> idents.size() == 3)
                .verifyComplete();
    }

    @Test
    void validerProduksjonIdenterUgyldig() {

        val ident = "01010112345";
        when(tpsMessagingConsumer.getIdenterProdStatus(anySet())).thenReturn(Flux.just(TpsStatusDTO.builder()
                .ident(ident)
                .inUse(true)
                .build()));

        webTestClient.post()
                .uri(IDENT_V2_BASEURL + "/validerflere")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"identer\":\"" + ident + "\"}")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.[0].ident").isEqualTo(ident)
                .jsonPath("$.[0].erGyldig").isEqualTo(false);
    }

    @Test
    void validerProduksjonIdenterGyldig() {

        val ident = "10108000398";
        when(tpsMessagingConsumer.getIdenterProdStatus(anySet())).thenReturn(Flux.just(TpsStatusDTO.builder()
                .ident(ident)
                .inUse(true)
                .build()));

        webTestClient.post()
                .uri(IDENT_V2_BASEURL + "/validerflere")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"identer\":\"" + ident + "\"}")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.[0].ident").isEqualTo(ident)
                .jsonPath("$.[0].erGyldig").isEqualTo(true)
                .jsonPath("$.[0].identtype").isEqualTo(Identtype.FNR)
                .jsonPath("$.[0].erIProd").isEqualTo(true)
                .jsonPath("$.[0].foedselsdato").isEqualTo("1980-10-10")
                .jsonPath("$.[0].erSyntetisk").isEqualTo(false)
                .jsonPath("$.[0].erPersonnummer2032").isEqualTo(null)
                .jsonPath("$.[0].erTestnorgeIdent").isEqualTo(null)
                .jsonPath("$.[0].kjoenn").isEqualTo(Kjoenn.MANN);
    }

    @Test
    void validerProduksjonIdenterHentingFeilet() {

        val ident = "10108000398";
        when(tpsMessagingConsumer.getIdenterProdStatus(anySet())).thenReturn(Flux.empty());

        webTestClient.post()
                .uri(IDENT_V2_BASEURL + "/validerflere")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"identer\":\"" + ident + "\"}")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.[0].ident").isEqualTo(ident)
                .jsonPath("$.[0].erGyldig").isEqualTo(true)
                .jsonPath("$.[0].identtype").isEqualTo(Identtype.FNR)
                .jsonPath("$.[0].erIProd").isEqualTo(null)
                .jsonPath("$.[0].foedselsdato").isEqualTo("1980-10-10")
                .jsonPath("$.[0].erSyntetisk").isEqualTo(false)
                .jsonPath("$.[0].erPersonnummer2032").isEqualTo(null)
                .jsonPath("$.[0].erTestnorgeIdent").isEqualTo(null)
                .jsonPath("$.[0].kjoenn").isEqualTo(Kjoenn.MANN)
                .jsonPath("$.[0].feilmelding").isEqualTo("Feil ved henting fra prod, forsøk igjen!");
    }

    @Test
    void validerSyntetiskeIdenterGyldig() {

        val ident = "09448542138";

        webTestClient.post()
                .uri(IDENT_V2_BASEURL + "/validerflere")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"identer\":\"" + ident + "\"}")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.[0].ident").isEqualTo(ident)
                .jsonPath("$.[0].erGyldig").isEqualTo(true)
                .jsonPath("$.[0].identtype").isEqualTo(Identtype.FNR)
                .jsonPath("$.[0].erIProd").isEqualTo(null)
                .jsonPath("$.[0].foedselsdato").isEqualTo("1985-04-09")
                .jsonPath("$.[0].erSyntetisk").isEqualTo(true)
                .jsonPath("$.[0].erPersonnummer2032").isEqualTo(false)
                .jsonPath("$.[0].erTestnorgeIdent").isEqualTo(false)
                .jsonPath("$.[0].kjoenn").isEqualTo(Kjoenn.MANN);
    }

    @Test
    void validerId2032IdenterGyldig() {

        val ident = "14505899947";

        webTestClient.post()
                .uri(IDENT_V2_BASEURL + "/validerflere")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"identer\":\"" + ident + "\"}")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.[0].ident").isEqualTo(ident)
                .jsonPath("$.[0].erGyldig").isEqualTo(true)
                .jsonPath("$.[0].identtype").isEqualTo(Identtype.FNR)
                .jsonPath("$.[0].erIProd").isEqualTo(null)
                .jsonPath("$.[0].foedselsdato").isEqualTo("1958-10-14")
                .jsonPath("$.[0].erSyntetisk").isEqualTo(true)
                .jsonPath("$.[0].erPersonnummer2032").isEqualTo(true)
                .jsonPath("$.[0].erTestnorgeIdent").isEqualTo(false)
                .jsonPath("$.[0].kjoenn").isEqualTo(null);
    }

    @Test
    void validerTestnorgeIdenterGyldig() {

        val ident = "61867501451";

        webTestClient.post()
                .uri(IDENT_V2_BASEURL + "/validerflere")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"identer\":\"" + ident + "\"}")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.[0].ident").isEqualTo(ident)
                .jsonPath("$.[0].erGyldig").isEqualTo(true)
                .jsonPath("$.[0].identtype").isEqualTo(Identtype.DNR)
                .jsonPath("$.[0].erIProd").isEqualTo(null)
                .jsonPath("$.[0].foedselsdato").isEqualTo("1975-06-21")
                .jsonPath("$.[0].erSyntetisk").isEqualTo(true)
                .jsonPath("$.[0].erPersonnummer2032").isEqualTo(false)
                .jsonPath("$.[0].erTestnorgeIdent").isEqualTo(true)
                .jsonPath("$.[0].kjoenn").isEqualTo(Kjoenn.KVINNE);
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