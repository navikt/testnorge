package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdsadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO.AdresseBeskyttelse.FORTROLIG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO.AdresseBeskyttelse.STRENGT_FORTROLIG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO.AdresseBeskyttelse.STRENGT_FORTROLIG_UTLAND;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AdressebeskyttelseServiceTest {

    private static final String DNR_IDENT = "45027812345";

    @InjectMocks
    private AdressebeskyttelseService adressebeskyttelseService;

    @Test
    void whenStrengtFortroligUtlandAndMasterIsFreg_thenThrowExecption() {

        var request = AdressebeskyttelseDTO.builder()
                .gradering(STRENGT_FORTROLIG_UTLAND)
                .master(Master.FREG)
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                adressebeskyttelseService.validate(request, PersonDTO.builder()
                        .ident(DNR_IDENT)
                        .build()));

        assertThat(exception.getMessage(), containsString("Gradering STRENGT_FORTROLIG_UTLAND kan kun settes hvis master er PDL"));
    }

    @Test
    void whenStrengtFortroligOrFortroligAndIdentypeDnr_thenThrowExecption() {

        var request = AdressebeskyttelseDTO.builder()
                .gradering(FORTROLIG)
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                adressebeskyttelseService.validate(request, PersonDTO.builder()
                        .ident(DNR_IDENT)
                        .build()));

        assertThat(exception.getMessage(), containsString("Adressebeskyttelse: Gradering " +
                "FORTROLIG kan kun settes på personer med fødselsnummer"));
    }

    @Test
    void whenStrengtFortroligUtland_thenSetMasterToPdl() {

        var request = PersonDTO.builder()
                .ident(DNR_IDENT)
                .adressebeskyttelse(List.of(AdressebeskyttelseDTO.builder()
                        .gradering(STRENGT_FORTROLIG_UTLAND)
                        .isNew(true)
                        .build()))
                .build();

        var target = adressebeskyttelseService.convert(request).getFirst();

        assertThat(target.getMaster(), is(equalTo(Master.PDL)));
    }

    @Test
    void whenStrengtFortrolig_thenSetKontaktadresseClearOtherAdresses() {

        var request = PersonDTO.builder()
                .ident(DNR_IDENT)
                .adressebeskyttelse(List.of(AdressebeskyttelseDTO.builder()
                        .gradering(STRENGT_FORTROLIG)
                        .isNew(true)
                        .build()))
                .bostedsadresse(List.of(new BostedadresseDTO()))
                .oppholdsadresse(List.of(new OppholdsadresseDTO()))
                .kontaktadresse(List.of(new KontaktadresseDTO()))
                .build();

        var target = adressebeskyttelseService.convert(request).getFirst();

        assertThat(target.getMaster(), is(equalTo(Master.FREG)));
        assertThat(request.getBostedsadresse(), is(empty()));
        assertThat(request.getOppholdsadresse(), is(empty()));
        assertThat(request.getKontaktadresse().getFirst().getPostboksadresse().getPostboks(), is("2094"));
        assertThat(request.getKontaktadresse().getFirst().getPostboksadresse().getPostbokseier(), is("SOT6 Vika"));
        assertThat(request.getKontaktadresse().getFirst().getPostboksadresse().getPostnummer(), is("0125"));
    }
}