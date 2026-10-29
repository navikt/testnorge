package no.nav.testnav.kodeverkservice.service;

import no.nav.testnav.kodeverkservice.utility.GamleKommunerUtility;
import no.nav.testnav.libs.dto.kodeverkservice.v1.KodeverkAdjustedDTO;
import no.nav.testnav.libs.dto.kodeverkservice.v1.KodeverkAdjustedDTO.KodeAdjusted;
import no.nav.testnav.libs.dto.kodeverkservice.v1.KodeverkDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class KodeverkMedHistoriskeServiceTest {

    private static final String KOMMUNER = "Kommuner";
    private static final String AKTIV_KOMMUNENR = "9999";
    private static final String HISTORISK_KOMMUNENR = "0101";

    @Mock
    private KodeverkService kodeverkService;

    private KodeverkMedHistoriskeService kodeverkMedHistoriskeService;

    @BeforeEach
    void setUp() {
        kodeverkMedHistoriskeService = new KodeverkMedHistoriskeService(kodeverkService);
    }

    @Test
    void shouldMergeActiveAndHistoricalKommunerIntoMap() {

        given(kodeverkService.getKodeverkMap(KOMMUNER)).willReturn(Mono.just(KodeverkDTO.builder()
                .kodeverknavn(KOMMUNER)
                .kodeverk(Map.of(AKTIV_KOMMUNENR, "Aktiv kommune"))
                .build()));

        var result = kodeverkMedHistoriskeService.getKommunerMedHistoriske().block();

        assertThat(result.getKodeverknavn()).isEqualTo("KommunerMedHistoriske");
        assertThat(result.getKodeverk())
                .containsEntry(AKTIV_KOMMUNENR, "Aktiv kommune")
                .containsEntry(HISTORISK_KOMMUNENR, "Halden (historisk)")
                .hasSize(GamleKommunerUtility.getHistoriskeKommuner().size() + 1);

    }

    @Test
    void shouldReturnOnlyHistoricalKommunerWhenKodeverkIsEmpty() {

        given(kodeverkService.getKodeverkMap(KOMMUNER)).willReturn(Mono.just(KodeverkDTO.builder()
                .kodeverknavn(KOMMUNER)
                .build()));

        var result = kodeverkMedHistoriskeService.getKommunerMedHistoriske().block();

        assertThat(result.getKodeverk())
                .isEqualTo(GamleKommunerUtility.getHistoriskeKommuner());
    }

    @Test
    void shouldFailWhenActiveAndHistoricalKommuneShareNumber() {

        given(kodeverkService.getKodeverkMap(KOMMUNER)).willReturn(Mono.just(KodeverkDTO.builder()
                .kodeverknavn(KOMMUNER)
                .kodeverk(Map.of(HISTORISK_KOMMUNENR, "Aktiv kommune"))
                .build()));

        assertThatThrownBy(() -> kodeverkMedHistoriskeService.getKommunerMedHistoriske().block())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldPropagateErrorWhenKodeverkMapFails() {

        given(kodeverkService.getKodeverkMap(KOMMUNER))
                .willReturn(Mono.error(new IllegalStateException("Kodeverk nede")));

        assertThatThrownBy(() -> kodeverkMedHistoriskeService.getKommunerMedHistoriske().block())
                .hasMessage("Kodeverk nede");
    }

    @Test
    void shouldAppendHistoricalKommunerAfterActiveInAdjustedKodeverk() {

        var aktivKommune = KodeAdjusted.builder()
                .label("Aktiv kommune")
                .value(AKTIV_KOMMUNENR)
                .gyldigFra(LocalDate.parse("2020-01-01"))
                .build();
        given(kodeverkService.getKodeverkByName(KOMMUNER)).willReturn(Mono.just(KodeverkAdjustedDTO.builder()
                .name(KOMMUNER)
                .koder(List.of(aktivKommune))
                .build()));

        var result = kodeverkMedHistoriskeService.getKommunerMedHistoriskeAdjusted().block();

        assertThat(result.getName()).isEqualTo("KommunerMedHistoriske");
        assertThat(result.getKoder()).hasSize(
                    GamleKommunerUtility.getHistoriskeKommunerAdjusted().size() + 1);
        assertThat(result.getKoder().getFirst()).isEqualTo(aktivKommune);
        assertThat(result.getKoder().subList(1, result.getKoder().size()))
                .containsExactlyElementsOf(GamleKommunerUtility.getHistoriskeKommunerAdjusted());

    }

    @Test
    void shouldIncludeValidityPeriodForHistoricalKommunerInAdjustedKodeverk() {

        given(kodeverkService.getKodeverkByName(KOMMUNER)).willReturn(Mono.just(KodeverkAdjustedDTO.builder()
                .name(KOMMUNER)
                .build()));

        var result = kodeverkMedHistoriskeService.getKommunerMedHistoriskeAdjusted().block();

        assertThat(result.getKoder())
                .filteredOn(kode -> HISTORISK_KOMMUNENR.equals(kode.getValue()))
                .singleElement()
                .satisfies(kode -> {
                    assertThat(kode.getLabel()).isEqualTo("Halden (historisk)");
                    assertThat(kode.getGyldigFra()).isEqualTo(LocalDate.parse("2000-01-01"));
                    assertThat(kode.getGyldigTil()).isEqualTo(LocalDate.parse("2019-12-31"));
                });
    }

    @Test
    void shouldKeepKommune1507InAdjustedKodeverk() {

        given(kodeverkService.getKodeverkByName(KOMMUNER)).willReturn(Mono.just(KodeverkAdjustedDTO.builder()
                .name(KOMMUNER)
                .build()));

        var result = kodeverkMedHistoriskeService.getKommunerMedHistoriskeAdjusted().block();

        assertThat(result.getKoder())
                .extracting(KodeAdjusted::getValue)
                .contains("1507");
    }

    @Test
    void shouldPropagateErrorWhenAdjustedKodeverkFails() {

        given(kodeverkService.getKodeverkByName(KOMMUNER))
                .willReturn(Mono.error(new IllegalStateException("Kodeverk nede")));

        assertThatThrownBy(() -> kodeverkMedHistoriskeService.getKommunerMedHistoriskeAdjusted().block())
                .hasMessage("Kodeverk nede");
    }
}
