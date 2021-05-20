package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.domain.PdlStatsborgerskap;
import no.nav.pdl.forvalter.dto.RsInnflytting;
import no.nav.pdl.forvalter.utils.TilfeldigLandService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsborgerskapServiceTest {

    private static final String FNR_IDENT = "12045612301";
    private static final String DNR_IDENT = "42045612301";

    @Mock
    private TilfeldigLandService tilfeldigLandService;

    @InjectMocks
    private StatsborgerskapService statsborgerskapService;

    @Test
    void whenUgyldigLandkode_thenThrowExecption() {

        var request = List.of(PdlStatsborgerskap.builder()
                        .landkode("Uruguay")
                        .isNew(true)
                        .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                statsborgerskapService.convert((List<PdlStatsborgerskap>) request, FNR_IDENT, null));

        assertThat(exception.getMessage(), containsString("Ugyldig landkode, må være i hht ISO-3 Landkoder"));
    }

    @Test
    void whenInvalidDateInterval_thenThrowExecption() {

        var request = List.of(PdlStatsborgerskap.builder()
                .gyldigFom(LocalDate.of(2020, 1, 1).atStartOfDay())
                .gyldigTom(LocalDate.of(2018, 1, 1).atStartOfDay())
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                statsborgerskapService.convert((List<PdlStatsborgerskap>) request, FNR_IDENT, null));

        assertThat(exception.getMessage(), containsString("Ugyldig datointervall: gyldigFom må være før gyldigTom"));
    }

    @Test
    void whenLandkodeIsEmptyAndAvailFromInnflytting_thenPickLandkodeFromInnflytting() {

        var request = List.of(PdlStatsborgerskap.builder()
                .isNew(true)
                .build());

        var target = statsborgerskapService.convert((List<PdlStatsborgerskap>) request,
                FNR_IDENT, RsInnflytting.builder()
                .fraflyttingsland("GER")
                .build())
                .get(0);

        assertThat(target.getLandkode(), is(equalTo("GER")));
    }

    @Test
    void whenLandkodeIsEmptyAndUnavailFromInnflyttingAndIdenttypeFNR_thenSetLandkodeNorge() {

        var target = statsborgerskapService.convert(List.of(PdlStatsborgerskap.builder()
                .isNew(true)
                .build()), FNR_IDENT, null)
                .get(0);

        assertThat(target.getLandkode(), is(equalTo("NOR")));
    }

    @Test
    void whenLandkodeIsEmptyAndUnavailFromInnflyttingAndIdenttypeDNR_thenTilfeldigLandServiceIsCalled() {

        when(tilfeldigLandService.getLand()).thenReturn("CHL");

        var target = statsborgerskapService.convert(List.of(PdlStatsborgerskap.builder()
                .isNew(true)
                .build()), DNR_IDENT, null)
                .get(0);

        verify(tilfeldigLandService).getLand();

        assertThat(target.getLandkode(), is(equalTo("CHL")));
    }

    @Test
    void whenGyldigFomNotProvided_thenDeriveGyldigFomFromBirthdate() {

        var target = statsborgerskapService.convert(List.of(PdlStatsborgerskap.builder()
                .isNew(true)
                .build()), FNR_IDENT, null)
                .get(0);

        assertThat(target.getGyldigFom(), is(equalTo(LocalDate.of(1956, 4, 12).atStartOfDay())));
    }
}