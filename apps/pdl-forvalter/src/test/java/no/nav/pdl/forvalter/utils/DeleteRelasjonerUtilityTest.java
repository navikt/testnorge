package no.nav.pdl.forvalter.utils;

import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.testnav.libs.data.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle;
import no.nav.testnav.libs.data.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.ForeldreansvarDTO.Ansvar;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(MockitoExtension.class)
class DeleteRelasjonerUtilityTest {

    private static final String IDENT_MOR = "11111111111";
    private static final String IDENT_FAR = "22222222222";
    private static final String IDENT_BARN = "33333333333";

    private DbPerson mor;
    private DbPerson far;
    private DbPerson barn;

    @BeforeEach
    void setup() {

        mor = DbPerson.builder()
                .ident(IDENT_MOR)
                .person(PersonDTO.builder()
                        .forelderBarnRelasjon(new ArrayList<>(List.of(
                                buildForeldreBarnRelasjon(Rolle.MOR, IDENT_BARN, Rolle.BARN))))
                        .build())
                .build();

        barn = DbPerson.builder()
                .ident(IDENT_BARN)
                .person(PersonDTO.builder()
                        .forelderBarnRelasjon(new ArrayList<>(List.of(
                                buildForeldreBarnRelasjon(Rolle.BARN, IDENT_MOR, Rolle.MOR),
                                buildForeldreBarnRelasjon(Rolle.BARN, IDENT_FAR, Rolle.FAR))))
                        .foreldreansvar(new ArrayList<>(List.of(buildForeldreAnsvar(Ansvar.FAR, IDENT_FAR))))
                        .build())
                .build();

        far = DbPerson.builder()
                .ident(IDENT_FAR)
                .person(PersonDTO.builder()
                        .forelderBarnRelasjon(new ArrayList<>(List.of(
                                buildForeldreBarnRelasjon(Rolle.FAR, IDENT_BARN, Rolle.BARN))))
                        .build())
                .build();

        mor.setRelasjoner(new ArrayList<>(List.of(buildDbRelasjon(mor, RelasjonType.FAMILIERELASJON_BARN, barn))));

        barn.setRelasjoner(new ArrayList<>(List.of(
                buildDbRelasjon(barn, RelasjonType.FAMILIERELASJON_FORELDER, mor),
                buildDbRelasjon(barn, RelasjonType.FAMILIERELASJON_FORELDER, far),
                buildDbRelasjon(barn, RelasjonType.FORELDREANSVAR_FORELDER, far))));

        far.setRelasjoner(new ArrayList<>(List.of(buildDbRelasjon(far, RelasjonType.FAMILIERELASJON_BARN, barn),
                buildDbRelasjon(far, RelasjonType.FORELDREANSVAR_BARN, barn))));
    }

    @Test
    void slettefamilieRelasjonMorBarn() {

        DeleteRelasjonerUtility.deleteRelasjoner(mor, barn, RelasjonType.FAMILIERELASJON_BARN);

        assertThat(mor.getRelasjoner(), hasSize(0));
        assertThat(mor.getPerson().getForelderBarnRelasjon(), hasSize(0));

        assertThat(barn.getRelasjoner(), hasSize(2));
        assertThat(barn.getPerson().getForelderBarnRelasjon(), hasSize(1));

        assertThat(far.getRelasjoner(), hasSize(2));
        assertThat(far.getPerson().getForelderBarnRelasjon(), hasSize(1));
    }

    @Test
    void sletteforeldreansvarFarBarn() {

        DeleteRelasjonerUtility.deleteRelasjoner(far, barn, RelasjonType.FORELDREANSVAR_BARN);

        assertThat(mor.getRelasjoner(), hasSize(1));
        assertThat(mor.getPerson().getForelderBarnRelasjon(), hasSize(1));

        assertThat(barn.getRelasjoner(), hasSize(2));
        assertThat(barn.getPerson().getForelderBarnRelasjon(), hasSize(2));
        assertThat(barn.getPerson().getForeldreansvar(), hasSize(0));

        assertThat(far.getRelasjoner(), hasSize(1));
        assertThat(far.getPerson().getForeldreansvar(), hasSize(0));
    }

    private static DbRelasjon buildDbRelasjon(DbPerson person, RelasjonType type, DbPerson relatertPerson) {

        return DbRelasjon.builder()
                .person(person)
                .relasjonType(type)
                .relatertPerson(relatertPerson)
                .build();
    }

    private static ForelderBarnRelasjonDTO buildForeldreBarnRelasjon(Rolle minRolle,
                                                                     String relatertIdent,
                                                                     Rolle relatertRolle) {

        return ForelderBarnRelasjonDTO.builder()
                .minRolleForPerson(minRolle)
                .relatertPerson(relatertIdent)
                .relatertPersonsRolle(relatertRolle)
                .build();
    }

    private static ForeldreansvarDTO buildForeldreAnsvar(Ansvar ansvar,
                                                         String relatertIdent) {

        return ForeldreansvarDTO.builder()
                .ansvar(ansvar)
                .ansvarlig(relatertIdent)
                .build();
    }
}