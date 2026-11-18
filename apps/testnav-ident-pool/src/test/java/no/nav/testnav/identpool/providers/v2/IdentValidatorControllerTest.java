package no.nav.testnav.identpool.providers.v2;

import lombok.val;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.identpool.config.AbstractTestcontainer;
import no.nav.testnav.identpool.consumers.TpsMessagingConsumer;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Kjoenn;
import no.nav.testnav.identpool.dto.TpsStatusDTO;
import no.nav.testnav.identpool.dto.ValideringFlereRequestDTO;
import no.nav.testnav.identpool.service.PersonnummerValidatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@DollySpringBootTest
class IdentValidatorControllerTest extends AbstractTestcontainer {

    private static final String VALIDATION_URL = "/api/v2/ident/validerFlere";

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private TpsMessagingConsumer tpsMessagingConsumer;

    @MockitoSpyBean
    private PersonnummerValidatorService personnummerValidatorService;

    @Test
    void validerProduksjonIdenterUgyldig() {

        val ident = "01010112345";
        when(tpsMessagingConsumer.getIdenterProdStatus(anySet())).thenReturn(Flux.just(TpsStatusDTO.builder()
                .ident(ident)
                .inUse(true)
                .build()));

        webTestClient.post()
                .uri(VALIDATION_URL)
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
                .uri(VALIDATION_URL)
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
        when(tpsMessagingConsumer.getIdenterProdStatus(anySet())).thenReturn(Flux.just(TpsStatusDTO.builder()
                .ident(ident)
                .inUse(false)
                .dirty(true)
                .build()));

        webTestClient.post()
                .uri(VALIDATION_URL)
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
                .uri(VALIDATION_URL)
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
                .uri(VALIDATION_URL)
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
                .uri(VALIDATION_URL)
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
    void shouldSplitMixedSeparatorsAndIgnoreEmptyEntries() {
        var firstIdent = "14505899947";
        var secondIdent = "61867501451";

        webTestClient.post()
                .uri(VALIDATION_URL)
                .bodyValue(new ValideringFlereRequestDTO(" ,;" + firstIdent + ",;\n\t " + secondIdent + " ; "))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2);

        verify(personnummerValidatorService).validerFoedselsnummer(List.of(firstIdent, secondIdent));
        verifyNoInteractions(tpsMessagingConsumer);
    }

    @ParameterizedTest
    @ValueSource(ints = {79, 80, 81, 161})
    void shouldProcessAllIdentifiersInBatchesOfAtMostEighty(int count) {
        var ident = "14505899947";

        webTestClient.post()
                .uri(VALIDATION_URL)
                .bodyValue(new ValideringFlereRequestDTO(String.join(" ", Collections.nCopies(count, ident))))
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$").isArray();

        var order = inOrder(personnummerValidatorService);
        if (count >= 80) {
            order.verify(personnummerValidatorService, times(count / 80))
                    .validerFoedselsnummer(Collections.nCopies(80, ident));
        }
        if (count % 80 != 0) {
            order.verify(personnummerValidatorService).validerFoedselsnummer(Collections.nCopies(count % 80, ident));
        }
        verifyNoMoreInteractions(personnummerValidatorService);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void shouldRejectMissingOrBlankIdentifiers(String identifiers) {
        webTestClient.post()
                .uri(VALIDATION_URL)
                .bodyValue(new ValideringFlereRequestDTO(identifiers))
                .exchange()
                .expectStatus().isBadRequest();

        verifyNoInteractions(personnummerValidatorService, tpsMessagingConsumer);
    }
}