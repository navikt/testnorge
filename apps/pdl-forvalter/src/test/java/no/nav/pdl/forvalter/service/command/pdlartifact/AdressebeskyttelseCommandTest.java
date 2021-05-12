package no.nav.pdl.forvalter.service.command.pdlartifact;

import no.nav.pdl.forvalter.domain.PdlAdressebeskyttelse;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static no.nav.pdl.forvalter.domain.PdlAdresse.Master.FREG;
import static no.nav.pdl.forvalter.domain.PdlAdresse.Master.PDL;
import static no.nav.pdl.forvalter.domain.PdlAdressebeskyttelse.AdresseBeskyttelse.STRENGT_FORTROLIG_UTLAND;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AdressebeskyttelseCommandTest {

    @Test
    void whenStrengtFortroligUtlandAndMasterIsFreg_thenThrowExecption() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                new AdressebeskyttelseCommand(List.of(PdlAdressebeskyttelse.builder()
                        .gradering(STRENGT_FORTROLIG_UTLAND)
                        .master(FREG)
                        .isNew(true)
                        .build())).call());

        assertThat(exception.getMessage(), containsString("Gradering STRENGT_FORTROLIG_UTLAND kan kun settes hvis master er PDL"));
    }

    @Test
    void whenStrengtFortroligUtlandSetMasterToPdl_thenThrowExecption() {

        var target = new AdressebeskyttelseCommand(List.of(PdlAdressebeskyttelse.builder()
                .gradering(STRENGT_FORTROLIG_UTLAND)
                .isNew(true)
                .build())).call().get(0);

        assertThat(target.getMaster(), is(equalTo(PDL)));
    }
}