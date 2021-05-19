package no.nav.pdl.forvalter.service.command.pdlartifact;

import no.nav.pdl.forvalter.domain.PdlStatsborgerskap;
import no.nav.pdl.forvalter.dto.RsInnflytting;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StatsborgerskapCommandTest {

    private static final String FNR_IDENT = "12045612301";
    private static final String DNR_IDENT = "42045612301";

    @Test
    void whenUgyldigLandkode_thenThrowExecption() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                new StatsborgerskapCommand(List.of(PdlStatsborgerskap.builder()
                        .landkode("Uruguay")
                        .isNew(true)
                        .build()), FNR_IDENT, null).call());

        assertThat(exception.getMessage(), containsString("Ugyldig landkode, må være i hht ISO-3 Landkoder"));
    }

    @Test
    void whenInvalidDateInterval_thenThrowExecption() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                new StatsborgerskapCommand(List.of(PdlStatsborgerskap.builder()
                        .gyldigFom(LocalDate.of(2020, 1, 1).atStartOfDay())
                        .gyldigTom(LocalDate.of(2018, 1, 1).atStartOfDay())
                        .isNew(true)
                        .build()), FNR_IDENT, null).call());

        assertThat(exception.getMessage(), containsString("Ugyldig datointervall: gyldigFom må være før gyldigTom"));
    }

    @Test
    void whenLandkodeIsEmptyAndAvailFromInnflytting_thenPickLandkodeFromInnflytting() {

        var target = new StatsborgerskapCommand(List.of(PdlStatsborgerskap.builder()
                .isNew(true)
                .build()), FNR_IDENT, RsInnflytting.builder()
                .fraflyttingsland("GER")
                .build())
                .call().get(0);

        assertThat(target.getLandkode(), is(equalTo("GER")));
    }

    @Test
    void whenLandkodeIsEmptyAndUnavailFromInnflyttingAndIdenttypeFNR_thenSetLandkodeNorge() {

        var target = new StatsborgerskapCommand(List.of(PdlStatsborgerskap.builder()
                .isNew(true)
                .build()), FNR_IDENT, null)
                .call().get(0);

        assertThat(target.getLandkode(), is(equalTo("NOR")));
    }

    @Test
    void whenLandkodeIsEmptyAndUnavailFromInnflyttingAndIdenttypeDNR_thenSetRandomLandkode() {

        var target = new StatsborgerskapCommand(List.of(PdlStatsborgerskap.builder()
                .isNew(true)
                .build()), DNR_IDENT, null)
                .call().get(0);

        assertThat(target.getLandkode(), hasLength(3));
    }

    @Test
    void whenGyldigFomNotProvided_thenDeriveGyldigFomFromBirthdate() {

        var target = new StatsborgerskapCommand(List.of(PdlStatsborgerskap.builder()
                .isNew(true)
                .build()), FNR_IDENT, null)
                .call().get(0);

        assertThat(target.getGyldigFom(), is(equalTo(LocalDate.of(1956, 4, 12).atStartOfDay())));
    }
}