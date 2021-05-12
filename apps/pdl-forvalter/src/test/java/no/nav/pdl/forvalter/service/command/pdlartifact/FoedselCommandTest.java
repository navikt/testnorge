package no.nav.pdl.forvalter.service.command.pdlartifact;

import no.nav.pdl.forvalter.domain.PdlBostedadresse;
import no.nav.pdl.forvalter.domain.PdlFoedsel;
import no.nav.pdl.forvalter.domain.PdlMatrikkeladresse;
import no.nav.pdl.forvalter.domain.PdlUtenlandskAdresse;
import no.nav.pdl.forvalter.domain.PdlVegadresse;
import no.nav.pdl.forvalter.dto.RsInnflytting;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.is;

class FoedselCommandTest {

    private static final String FNR_IDENT ="12105628901";
    private static final String DNR_IDENT ="41056828901";

    private static final PdlBostedadresse vegadresse = PdlBostedadresse.builder()
            .vegadresse(PdlVegadresse.builder().kommunenummer("3025").build())
            .build();
    private static final PdlBostedadresse matrikkeladresse = PdlBostedadresse.builder()
            .matrikkeladresse(PdlMatrikkeladresse.builder().kommunenummer("3024").build())
            .build();
    private static final PdlBostedadresse ukjentBosted = PdlBostedadresse.builder()
            .ukjentBosted(PdlBostedadresse.PdlUkjentBosted.builder().bostedskommune("4644").build())
            .build();
    private static final PdlBostedadresse utenlandskBoadresse = PdlBostedadresse.builder()
            .utenlandskAdresse(PdlUtenlandskAdresse.builder().landkode("BRA").build())
            .build();
    private static final RsInnflytting innflytting = RsInnflytting.builder()
            .fraflyttingsland("JPN")
            .build();

    @Test
    void whenIdentIsFnrAndVegadresseConveysKommune_thenCaptureFoedekommune() {

        var target = new FoedselCommand(List.of(PdlFoedsel.builder()
                .isNew(true)
                .build()), FNR_IDENT, vegadresse, null)
                .call().get(0);

        assertThat(target.getFoedselsdato(), is(equalTo(LocalDate.of(1956, 10,12).atStartOfDay())));
        assertThat(target.getFoedselsaar(), is(equalTo(1956)));
        assertThat(target.getFoedeland(), is(equalTo("NOR")));
        assertThat(target.getFodekommune(), is(equalTo("3025")));
    }

    @Test
    void whenIdentIsFnrAndMatrikkeldresseConveysKommune_thenCaptureFoedekommune() {

        var target = new FoedselCommand(List.of(PdlFoedsel.builder()
                .isNew(true)
                .build()), FNR_IDENT, matrikkeladresse, null)
                .call().get(0);

        assertThat(target.getFoedselsdato(), is(equalTo(LocalDate.of(1956, 10,12).atStartOfDay())));
        assertThat(target.getFoedselsaar(), is(equalTo(1956)));
        assertThat(target.getFoedeland(), is(equalTo("NOR")));
        assertThat(target.getFodekommune(), is(equalTo("3024")));
    }

    @Test
    void whenIdentIsFnrAndUkjentBostedConveysKommune_thenCaptureFoedekommune() {

        var target = new FoedselCommand(List.of(PdlFoedsel.builder()
                .isNew(true)
                .build()), FNR_IDENT, ukjentBosted, null)
                .call().get(0);

        assertThat(target.getFoedselsdato(), is(equalTo(LocalDate.of(1956, 10,12).atStartOfDay())));
        assertThat(target.getFoedselsaar(), is(equalTo(1956)));
        assertThat(target.getFoedeland(), is(equalTo("NOR")));
        assertThat(target.getFodekommune(), is(equalTo("4644")));
    }

    @Test
    void whenIdentIsFnrAndKommuneOfBirthIsUnknown_thenProvideRandomFoedekommune() {

        var target = new FoedselCommand(List.of(PdlFoedsel.builder()
                .isNew(true)
                .build()), FNR_IDENT, null, null)
                .call().get(0);

        assertThat(target.getFoedselsdato(), is(equalTo(LocalDate.of(1956, 10,12).atStartOfDay())));
        assertThat(target.getFoedselsaar(), is(equalTo(1956)));
        assertThat(target.getFoedeland(), is(equalTo("NOR")));
        assertThat(target.getFodekommune(), hasLength(4));
    }

    @Test
    void whenIdentIsDnrAndUtenLandskAdresseConveysCountry_thenCapturLandkode() {

        var target = new FoedselCommand(List.of(PdlFoedsel.builder()
                .isNew(true)
                .build()), DNR_IDENT, utenlandskBoadresse, null)
                .call().get(0);

        assertThat(target.getFoedselsdato(), is(equalTo(LocalDate.of(1968, 5,1).atStartOfDay())));
        assertThat(target.getFoedselsaar(), is(equalTo(1968)));
        assertThat(target.getFoedeland(), is(equalTo("BRA")));
    }

    @Test
    void whenIdentIsDnrAndInnflyttingConveysCountry_thenCapturLandkode() {

        var target = new FoedselCommand(List.of(PdlFoedsel.builder()
                .isNew(true)
                .build()), DNR_IDENT, null, innflytting)
                .call().get(0);

        assertThat(target.getFoedselsdato(), is(equalTo(LocalDate.of(1968, 5,1).atStartOfDay())));
        assertThat(target.getFoedselsaar(), is(equalTo(1968)));
        assertThat(target.getFoedeland(), is(equalTo("JPN")));
    }

    @Test
    void whenIdentIsDnrAndLandOfBirthUnkown_thenProvideRandomLandkode() {

        var target = new FoedselCommand(List.of(PdlFoedsel.builder()
                .isNew(true)
                .build()), DNR_IDENT, null, null)
                .call().get(0);

        assertThat(target.getFoedselsdato(), is(equalTo(LocalDate.of(1968, 5,1).atStartOfDay())));
        assertThat(target.getFoedselsaar(), is(equalTo(1968)));
        assertThat(target.getFoedeland(), hasLength(3));
    }
}