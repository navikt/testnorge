package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.domain.BostedadresseDTO;
import no.nav.pdl.forvalter.domain.FoedselDTO;
import no.nav.pdl.forvalter.domain.InnflyttingDTO;
import no.nav.pdl.forvalter.domain.MatrikkeladresseDTO;
import no.nav.pdl.forvalter.domain.PersonDTO;
import no.nav.pdl.forvalter.domain.UtenlandskAdresseDTO;
import no.nav.pdl.forvalter.domain.VegadresseDTO;
import no.nav.pdl.forvalter.utils.TilfeldigKommuneService;
import no.nav.pdl.forvalter.utils.TilfeldigLandService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoedselServiceTest {

    private static final String FNR_IDENT = "12105628901";
    private static final String DNR_IDENT = "41056828901";

    private static final BostedadresseDTO vegadresse = BostedadresseDTO.builder()
            .vegadresse(VegadresseDTO.builder().kommunenummer("3025").build())
            .build();
    private static final BostedadresseDTO matrikkeladresse = BostedadresseDTO.builder()
            .matrikkeladresse(MatrikkeladresseDTO.builder().kommunenummer("3024").build())
            .build();
    private static final BostedadresseDTO ukjentBosted = BostedadresseDTO.builder()
            .ukjentBosted(BostedadresseDTO.UkjentBostedDTO.builder().bostedskommune("4644").build())
            .build();
    private static final BostedadresseDTO utenlandskBoadresse = BostedadresseDTO.builder()
            .utenlandskAdresse(UtenlandskAdresseDTO.builder().landkode("BRA").build())
            .build();
    private static final InnflyttingDTO innflytting = InnflyttingDTO.builder()
            .fraflyttingsland("JPN")
            .build();

    @Mock
    private TilfeldigKommuneService tilfeldigKommuneService;

    @Mock
    private TilfeldigLandService tilfeldigLandService;

    @InjectMocks
    private FoedselService foedselService;

    @Test
    void whenIdentIsFnrAndVegadresseConveysKommune_thenCaptureFoedekommune() {

        var target = foedselService.convert(PersonDTO.builder()
                .foedsel(List.of(FoedselDTO.builder()
                        .isNew(true)
                        .build()))
                .ident(FNR_IDENT)
                .bostedsadresse(List.of(vegadresse))
                .build())
                .get(0);

        assertThat(target.getFoedselsdato(), is(equalTo(LocalDate.of(1956, 10, 12).atStartOfDay())));
        assertThat(target.getFoedselsaar(), is(equalTo(1956)));
        assertThat(target.getFoedeland(), is(equalTo("NOR")));
        assertThat(target.getFodekommune(), is(equalTo("3025")));
    }

    @Test
    void whenIdentIsFnrAndMatrikkeldresseConveysKommune_thenCaptureFoedekommune() {

        var target = foedselService.convert(PersonDTO.builder()
                .foedsel(List.of(FoedselDTO.builder()
                        .isNew(true)
                        .build()))
                .ident(FNR_IDENT)
                .bostedsadresse(List.of(matrikkeladresse))
                .build())
                .get(0);

        assertThat(target.getFoedselsdato(), is(equalTo(LocalDate.of(1956, 10, 12).atStartOfDay())));
        assertThat(target.getFoedselsaar(), is(equalTo(1956)));
        assertThat(target.getFoedeland(), is(equalTo("NOR")));
        assertThat(target.getFodekommune(), is(equalTo("3024")));
    }

    @Test
    void whenIdentIsFnrAndUkjentBostedConveysKommune_thenCaptureFoedekommune() {

        var target = foedselService.convert(PersonDTO.builder()
                .foedsel(List.of(FoedselDTO.builder()
                        .isNew(true)
                        .build()))
                .ident(FNR_IDENT)
                .bostedsadresse(List.of(ukjentBosted))
                .build())
                .get(0);

        assertThat(target.getFoedselsdato(), is(equalTo(LocalDate.of(1956, 10, 12).atStartOfDay())));
        assertThat(target.getFoedselsaar(), is(equalTo(1956)));
        assertThat(target.getFoedeland(), is(equalTo("NOR")));
        assertThat(target.getFodekommune(), is(equalTo("4644")));
    }

    @Test
    void whenIdentIsFnrAndKommuneOfBirthIsUnknown_thenVerifyTilfeldigKommuneServiceCalled() {

        when(tilfeldigKommuneService.getKommune()).thenReturn("4777");

        var target = foedselService.convert(PersonDTO.builder()
                .foedsel(List.of(FoedselDTO.builder()
                        .isNew(true)
                        .build()))
                .ident(FNR_IDENT)
                .build())
                .get(0);

        verify(tilfeldigKommuneService).getKommune();

        assertThat(target.getFoedselsdato(), is(equalTo(LocalDate.of(1956, 10, 12).atStartOfDay())));
        assertThat(target.getFoedselsaar(), is(equalTo(1956)));
        assertThat(target.getFoedeland(), is(equalTo("NOR")));
        assertThat(target.getFodekommune(), is(equalTo("4777")));
    }

    @Test
    void whenIdentIsDnrAndUtenLandskAdresseConveysCountry_thenCaptureLandkode() {

        var target = foedselService.convert(PersonDTO.builder()
                .foedsel(List.of(FoedselDTO.builder()
                        .isNew(true)
                        .build()))
                .ident(DNR_IDENT)
                .bostedsadresse(List.of(utenlandskBoadresse))
                .build())
                .get(0);

        assertThat(target.getFoedselsdato(), is(equalTo(LocalDate.of(1968, 5, 1).atStartOfDay())));
        assertThat(target.getFoedselsaar(), is(equalTo(1968)));
        assertThat(target.getFoedeland(), is(equalTo("BRA")));
    }

    @Test
    void whenIdentIsDnrAndInnflyttingConveysCountry_thenCapturLandkode() {

        var target = foedselService.convert(PersonDTO.builder()
                .foedsel(List.of(FoedselDTO.builder()
                        .isNew(true)
                        .build()))
                .ident(DNR_IDENT)
                .innflytting(List.of(innflytting))
                .build())
                .get(0);

        assertThat(target.getFoedselsdato(), is(equalTo(LocalDate.of(1968, 5, 1).atStartOfDay())));
        assertThat(target.getFoedselsaar(), is(equalTo(1968)));
        assertThat(target.getFoedeland(), is(equalTo("JPN")));
    }

    @Test
    void whenIdentIsDnrAndLandOfBirthUnkown_thenLandkodeServiceIsCalled() {

        when(tilfeldigLandService.getLand()).thenReturn("COL");

        var target = foedselService.convert(PersonDTO.builder()
                .foedsel(List.of(FoedselDTO.builder()
                        .isNew(true)
                        .build()))
                .ident(DNR_IDENT)
                .build())
                .get(0);

        verify(tilfeldigLandService).getLand();

        assertThat(target.getFoedselsdato(), is(equalTo(LocalDate.of(1968, 5, 1).atStartOfDay())));
        assertThat(target.getFoedselsaar(), is(equalTo(1968)));
        assertThat(target.getFoedeland(), is(equalTo("COL")));
    }
}