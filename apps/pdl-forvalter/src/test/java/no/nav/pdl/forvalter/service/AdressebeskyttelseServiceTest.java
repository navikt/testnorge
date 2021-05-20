package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.domain.PdlAdressebeskyttelse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
class AdressebeskyttelseServiceTest {

    @InjectMocks
    private AdressebeskyttelseService adressebeskyttelseService;

    @Test
    void whenStrengtFortroligUtlandAndMasterIsFreg_thenThrowExecption() {

        var request = List.of(PdlAdressebeskyttelse.builder()
                .gradering(STRENGT_FORTROLIG_UTLAND)
                .master(FREG)
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                adressebeskyttelseService.convert((List<PdlAdressebeskyttelse>) request));

        assertThat(exception.getMessage(), containsString("Gradering STRENGT_FORTROLIG_UTLAND kan kun settes hvis master er PDL"));
    }

    @Test
    void whenStrengtFortroligUtlandSetMasterToPdl_thenThrowExecption() {

        var target = adressebeskyttelseService.convert(List.of(PdlAdressebeskyttelse.builder()
                .gradering(STRENGT_FORTROLIG_UTLAND)
                .isNew(true)
                .build())).get(0);

        assertThat(target.getMaster(), is(equalTo(PDL)));
    }
}