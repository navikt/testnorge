package no.nav.pdl.forvalter.utils;

import lombok.experimental.UtilityClass;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.testnav.libs.data.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType.AVDOEDD_FOR_KONTAKT;
import static no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType.EKTEFELLE_PARTNER;
import static no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType.FALSK_IDENTITET;
import static no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType.FAMILIERELASJON_BARN;
import static no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType.FAMILIERELASJON_FORELDER;
import static no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType.FORELDREANSVAR_BARN;
import static no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType.FORELDREANSVAR_FORELDER;
import static no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType.FULLMAKTSGIVER;
import static no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType.FULLMEKTIG;
import static no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType.GAMMEL_IDENTITET;
import static no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType.KONTAKT_FOR_DOEDSBO;
import static no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType.NY_IDENTITET;
import static no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType.RIKTIG_IDENTITET;
import static no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType.VERGE;
import static no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType.VERGE_MOTTAKER;

@UtilityClass
public class DeleteRelasjonerUtility {

    public static void deleteRelasjoner(DbPerson person, DbPerson relatertPerson, RelasjonType type) {

        var it = person.getRelasjoner().iterator();
        while (it.hasNext()) {
            var relasjon = it.next();

            if (isType(relasjon.getRelasjonType(), getRelasjonTyper(type)) &&
                    relasjon.getRelatertPerson().getIdent().equals(relatertPerson.getIdent())) {

                deleteRelasjon(relatertPerson, person.getIdent(), getRelasjonTyper(relasjon.getRelasjonType()));

                deleteOpplysningstype(person, relatertPerson.getIdent(), relasjon.getRelasjonType());
                deleteOpplysningstype(relatertPerson, person.getIdent(), relasjon.getRelasjonType());
                it.remove();
            }
        }
    }

    private static void deleteRelasjon(DbPerson person, String tidligereRelatert, RelasjonType... typer) {

        Iterator<DbRelasjon> it = person.getRelasjoner().iterator();
        while (it.hasNext()) {
            var relasjon = it.next();
            if (isType(relasjon.getRelasjonType(), typer) &&
                    relasjon.getPerson().getIdent().equals(person.getIdent()) &&
                    relasjon.getRelatertPerson().getIdent().equals(tidligereRelatert)) {

                it.remove();
            }
        }
    }

    private static void deleteOpplysningstype(DbPerson person, String relatertIdent, RelasjonType type) {

        getSetter(type).accept(person.getPerson(), new ArrayList<>(
                getGetter(type).apply(person.getPerson()).stream()
                        .filter(relasjon -> !relatertIdent.equals(relasjon.getIdentForRelasjon()))
                        .toList()));
    }

    private static boolean isType(RelasjonType relasjonType, RelasjonType[] typer) {

        return Arrays.asList(typer).contains(relasjonType);
    }

    private static RelasjonType[] getRelasjonTyper(RelasjonType relasjonType) {

        return switch (relasjonType) {

            case FULLMEKTIG, FULLMAKTSGIVER -> new RelasjonType[]{FULLMEKTIG, FULLMAKTSGIVER};
            case VERGE, VERGE_MOTTAKER -> new RelasjonType[]{VERGE, VERGE_MOTTAKER};
            case RIKTIG_IDENTITET, FALSK_IDENTITET -> new RelasjonType[]{RIKTIG_IDENTITET, FALSK_IDENTITET};
            case KONTAKT_FOR_DOEDSBO, AVDOEDD_FOR_KONTAKT ->
                    new RelasjonType[]{KONTAKT_FOR_DOEDSBO, AVDOEDD_FOR_KONTAKT};
            case NY_IDENTITET, GAMMEL_IDENTITET -> new RelasjonType[]{NY_IDENTITET, GAMMEL_IDENTITET};
            case EKTEFELLE_PARTNER -> new RelasjonType[]{EKTEFELLE_PARTNER};
            case FAMILIERELASJON_BARN, FAMILIERELASJON_FORELDER ->
                    new RelasjonType[]{FAMILIERELASJON_BARN, FAMILIERELASJON_FORELDER};
            case FORELDREANSVAR_BARN, FORELDREANSVAR_FORELDER ->
                    new RelasjonType[]{FORELDREANSVAR_BARN, FORELDREANSVAR_FORELDER};
        };
    }

    @SuppressWarnings("java:S3740")
    private static BiConsumer<PersonDTO, List> getSetter(RelasjonType relasjonType) {

        return switch (relasjonType) {

            case FULLMEKTIG, FULLMAKTSGIVER -> PersonDTO::setFullmakt;
            case VERGE, VERGE_MOTTAKER -> PersonDTO::setVergemaal;
            case RIKTIG_IDENTITET, FALSK_IDENTITET -> PersonDTO::setFalskIdentitet;
            case KONTAKT_FOR_DOEDSBO, AVDOEDD_FOR_KONTAKT -> PersonDTO::setKontaktinformasjonForDoedsbo;
            case NY_IDENTITET, GAMMEL_IDENTITET -> PersonDTO::setNyident;
            case EKTEFELLE_PARTNER -> PersonDTO::setSivilstand;
            case FAMILIERELASJON_BARN, FAMILIERELASJON_FORELDER -> PersonDTO::setForelderBarnRelasjon;
            case FORELDREANSVAR_BARN, FORELDREANSVAR_FORELDER -> PersonDTO::setForeldreansvar;
        };
    }

    private static Function<PersonDTO, List<? extends DbVersjonDTO>> getGetter(RelasjonType relasjonType) {

        return switch (relasjonType) {

            case FULLMEKTIG, FULLMAKTSGIVER -> PersonDTO::getFullmakt;
            case VERGE, VERGE_MOTTAKER -> PersonDTO::getVergemaal;
            case RIKTIG_IDENTITET, FALSK_IDENTITET -> PersonDTO::getFalskIdentitet;
            case KONTAKT_FOR_DOEDSBO, AVDOEDD_FOR_KONTAKT -> PersonDTO::getKontaktinformasjonForDoedsbo;
            case NY_IDENTITET, GAMMEL_IDENTITET -> PersonDTO::getNyident;
            case EKTEFELLE_PARTNER -> PersonDTO::getSivilstand;
            case FAMILIERELASJON_BARN, FAMILIERELASJON_FORELDER -> PersonDTO::getForelderBarnRelasjon;
            case FORELDREANSVAR_BARN, FORELDREANSVAR_FORELDER -> PersonDTO::getForeldreansvar;
        };
    }
}