package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdsadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
import reactor.test.StepVerifier;

import java.util.List;

import static no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO.AdresseBeskyttelse.FORTROLIG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO.AdresseBeskyttelse.STRENGT_FORTROLIG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO.AdresseBeskyttelse.STRENGT_FORTROLIG_UTLAND;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

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

        StepVerifier.create(adressebeskyttelseService.validate(request, PersonDTO.builder()
                        .ident(DNR_IDENT)
                        .build()))
                .verifyErrorSatisfies(throwable ->
                        Assertions.assertThat(throwable).isInstanceOf(HttpClientErrorException.class)
                                .hasMessageContaining("Gradering STRENGT_FORTROLIG_UTLAND kan kun settes hvis master er PDL"));
    }

    @Test
    void whenStrengtFortroligOrFortroligAndIdentypeDnr_thenThrowExecption() {

        var request = AdressebeskyttelseDTO.builder()
                .gradering(FORTROLIG)
                .isNew(true)
                .build();

        StepVerifier.create(adressebeskyttelseService.validate(request, PersonDTO.builder()
                        .ident(DNR_IDENT)
                        .build()))
                .verifyErrorSatisfies(throwable ->
                        Assertions.assertThat(throwable).isInstanceOf(HttpClientErrorException.class)
                                .hasMessageContaining("Gradering FORTROLIG kan kun settes på personer med fødselsnummer"));
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

        StepVerifier.create(adressebeskyttelseService.convert(request))
                .assertNext(person ->
                        assertThat(person.getAdressebeskyttelse().getFirst().getMaster(), is(equalTo(Master.PDL))))
                .verifyComplete();
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

        StepVerifier.create(adressebeskyttelseService.convert(request))
                .assertNext(target -> {

                    assertThat(target.getAdressebeskyttelse().getFirst().getMaster(), is(equalTo(Master.FREG)));
                    assertThat(target.getBostedsadresse(), is(empty()));
                    assertThat(target.getOppholdsadresse(), is(empty()));
                    assertThat(target.getKontaktadresse().getFirst().getPostboksadresse().getPostboks(), is("2094"));
                    assertThat(target.getKontaktadresse().getFirst().getPostboksadresse().getPostbokseier(), is("SOT6 Vika"));
                    assertThat(target.getKontaktadresse().getFirst().getPostboksadresse().getPostnummer(), is("0125"));
                })
                .verifyComplete();
    }
}