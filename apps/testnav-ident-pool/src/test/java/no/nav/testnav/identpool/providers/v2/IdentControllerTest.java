package no.nav.testnav.identpool.providers.v2;

import lombok.val;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.identpool.config.AbstractTestcontainer;
import no.nav.testnav.identpool.consumers.TpsMessagingConsumer;
import no.nav.testnav.identpool.domain.Ident2032;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Kjoenn;
import no.nav.testnav.identpool.dto.IdentpoolRequestDTO;
import no.nav.testnav.identpool.dto.IdentpoolResponseDTO;
import no.nav.testnav.identpool.dto.TpsStatusDTO;
import no.nav.testnav.identpool.dto.ValideringResponseDTO;
import no.nav.testnav.identpool.repository.PersonidentifikatorRepository;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
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
import static org.hamcrest.CoreMatchers.nullValue;
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

    @Test
    void validerProduksjonIdenterUgyldig() {

        val ident = "01010112345";
        when(tpsMessagingConsumer.getIdenterProdStatus(anySet())).thenReturn(Flux.just(TpsStatusDTO.builder()
                        .ident(ident)
                        .inUse(true)
                        .build()));

        val response = webTestClient.post()
                .uri(IDENT_V2_BASEURL + "/validerflere")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"identer\":\"" + ident + "\"}")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ValideringResponseDTO[].class)
                .returnResult()
                .getResponseBody();

        assertThat(response[0].ident(), is(equalTo(ident)));
        assertThat(response[0].erGyldig(), is(equalTo(false)));
    }

    @Test
    void validerProduksjonIdenterGyldig() {

        val ident = "10108000398";
        when(tpsMessagingConsumer.getIdenterProdStatus(anySet())).thenReturn(Flux.just(TpsStatusDTO.builder()
                .ident(ident)
                .inUse(true)
                .build()));

        val response = webTestClient.post()
                .uri(IDENT_V2_BASEURL + "/validerflere")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"identer\":\"" + ident + "\"}")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ValideringResponseDTO[].class)
                .returnResult()
                .getResponseBody();

        assertThat(response[0].ident(), is(equalTo(ident)));
        assertThat(response[0].erGyldig(), is(equalTo(true)));
        assertThat(response[0].identtype(), is(equalTo(Identtype.FNR)));
        assertThat(response[0].erIProd(), is(equalTo(true)));
        assertThat(response[0].foedselsdato(), is(equalTo(LocalDate.of(1980, 10, 10))));
        assertThat(response[0].erSyntetisk(), is(equalTo(false)));
        assertThat(response[0].erPersonnummer2032(), is(nullValue()));
        assertThat(response[0].erTestnorgeIdent(), is(nullValue()));
        assertThat(response[0].kjoenn(), is(equalTo(Kjoenn.MANN)));
    }

    @Test
    void validerSyntetiskeIdenterGyldig() {

        val ident = "09448542138";

        val response = webTestClient.post()
                .uri(IDENT_V2_BASEURL + "/validerflere")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"identer\":\"" + ident + "\"}")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ValideringResponseDTO[].class)
                .returnResult()
                .getResponseBody();

        assertThat(response[0].ident(), is(equalTo(ident)));
        assertThat(response[0].erGyldig(), is(equalTo(true)));
        assertThat(response[0].identtype(), is(equalTo(Identtype.FNR)));
        assertThat(response[0].erIProd(), is(nullValue()));
        assertThat(response[0].foedselsdato(), is(equalTo(LocalDate.of(1985, 4, 9))));
        assertThat(response[0].erSyntetisk(), is(equalTo(true)));
        assertThat(response[0].erPersonnummer2032(), is(equalTo(false)));
        assertThat(response[0].erTestnorgeIdent(), is(equalTo(false)));
        assertThat(response[0].kjoenn(), is(equalTo(Kjoenn.MANN)));
    }

    @Test
    void validerId2032IdenterGyldig() {

        val ident = "14505899947";

        val response = webTestClient.post()
                .uri(IDENT_V2_BASEURL + "/validerflere")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"identer\":\"" + ident + "\"}")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ValideringResponseDTO[].class)
                .returnResult()
                .getResponseBody();

        assertThat(response[0].ident(), is(equalTo(ident)));
        assertThat(response[0].erGyldig(), is(equalTo(true)));
        assertThat(response[0].identtype(), is(equalTo(Identtype.FNR)));
        assertThat(response[0].erIProd(), is(nullValue()));
        assertThat(response[0].foedselsdato(), is(equalTo(LocalDate.of(1958, 10, 14))));
        assertThat(response[0].erSyntetisk(), is(equalTo(true)));
        assertThat(response[0].erPersonnummer2032(), is(equalTo(true)));
        assertThat(response[0].erTestnorgeIdent(), is(equalTo(false)));
        assertThat(response[0].kjoenn(), is(nullValue()));
    }

    @Test
    void validerTestnorgeIdenterGyldig() {

        val ident = "61867501451";

        val response = webTestClient.post()
                .uri(IDENT_V2_BASEURL + "/validerflere")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"identer\":\"" + ident + "\"}")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ValideringResponseDTO[].class)
                .returnResult()
                .getResponseBody();

        assertThat(response[0].ident(), is(equalTo(ident)));
        assertThat(response[0].erGyldig(), is(equalTo(true)));
        assertThat(response[0].identtype(), is(equalTo(Identtype.DNR)));
        assertThat(response[0].erIProd(), is(nullValue()));
        assertThat(response[0].foedselsdato(), is(equalTo(LocalDate.of(1975, 6, 21))));
        assertThat(response[0].erSyntetisk(), is(equalTo(true)));
        assertThat(response[0].erPersonnummer2032(), is(equalTo(false)));
        assertThat(response[0].erTestnorgeIdent(), is(equalTo(true)));
        assertThat(response[0].kjoenn(), is(equalTo(Kjoenn.KVINNE)));
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