package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UkjentBostedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskAdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

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
            .ukjentBosted(UkjentBostedDTO.builder().bostedskommune("4644").build())
            .build();
    private static final BostedadresseDTO utenlandskBoadresse = BostedadresseDTO.builder()
            .utenlandskAdresse(UtenlandskAdresseDTO.builder().landkode("BRA").build())
            .build();
    private static final InnflyttingDTO innflytting = InnflyttingDTO.builder()
            .fraflyttingsland("JPN")
            .build();

    @Mock
    private KodeverkConsumer kodeverkConsumer;

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
                .getFirst();

        assertThat(target.getFoedselsdato(), is(equalTo(LocalDate.of(1956, 10, 12).atStartOfDay())));
        assertThat(target.getFoedselsaar(), is(equalTo(1956)));
        assertThat(target.getFoedeland(), is(equalTo("NOR")));
        assertThat(target.getFoedekommune(), is(equalTo("3025")));
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
                .getFirst();

        assertThat(target.getFoedselsdato(), is(equalTo(LocalDate.of(1956, 10, 12).atStartOfDay())));
        assertThat(target.getFoedselsaar(), is(equalTo(1956)));
        assertThat(target.getFoedeland(), is(equalTo("NOR")));
        assertThat(target.getFoedekommune(), is(equalTo("3024")));
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
                .getFirst();

        assertThat(target.getFoedselsdato(), is(equalTo(LocalDate.of(1956, 10, 12).atStartOfDay())));
        assertThat(target.getFoedselsaar(), is(equalTo(1956)));
        assertThat(target.getFoedeland(), is(equalTo("NOR")));
        assertThat(target.getFoedekommune(), is(equalTo("4644")));
    }

    @Test
    void whenIdentIsFnrAndKommuneOfBirthIsUnknown_thenVerifyGetTilfeldigKommuneIsCalled() {

        when(kodeverkConsumer.getTilfeldigKommune()).thenReturn(Mono.just("4777"));

        var target = foedselService.convert(PersonDTO.builder()
                        .foedsel(List.of(FoedselDTO.builder()
                                .isNew(true)
                                .build()))
                        .ident(FNR_IDENT)
                        .build())
                .getFirst();

        verify(kodeverkConsumer).getTilfeldigKommune();

        assertThat(target.getFoedselsdato(), is(equalTo(LocalDate.of(1956, 10, 12).atStartOfDay())));
        assertThat(target.getFoedselsaar(), is(equalTo(1956)));
        assertThat(target.getFoedeland(), is(equalTo("NOR")));
        assertThat(target.getFoedekommune(), is(equalTo("4777")));
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
                .getFirst();

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
                .getFirst();

        assertThat(target.getFoedselsdato(), is(equalTo(LocalDate.of(1968, 5, 1).atStartOfDay())));
        assertThat(target.getFoedselsaar(), is(equalTo(1968)));
        assertThat(target.getFoedeland(), is(equalTo("JPN")));
    }

    @Test
    void whenIdentIsDnrAndLandOfBirthUnkown_thenLandkodeServiceIsCalled() {

        when(kodeverkConsumer.getTilfeldigLand()).thenReturn(Mono.just("COL"));

        var target = foedselService.convert(PersonDTO.builder()
                        .foedsel(List.of(FoedselDTO.builder()
                                .isNew(true)
                                .build()))
                        .ident(DNR_IDENT)
                        .build())
                .getFirst();

        verify(kodeverkConsumer).getTilfeldigLand();

        assertThat(target.getFoedselsdato(), is(equalTo(LocalDate.of(1968, 5, 1).atStartOfDay())));
        assertThat(target.getFoedselsaar(), is(equalTo(1968)));
        assertThat(target.getFoedeland(), is(equalTo("COL")));
    }
}