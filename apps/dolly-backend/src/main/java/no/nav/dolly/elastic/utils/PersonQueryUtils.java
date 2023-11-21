package no.nav.dolly.elastic.utils;

import lombok.experimental.UtilityClass;
import no.nav.dolly.elastic.dto.SearchRequest;
import org.springframework.data.elasticsearch.core.query.Criteria;

import java.util.Optional;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class PersonQueryUtils {

    public static void addBarnQuery(Criteria criteria, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarBarn())) {
            criteria.and(getCriteria("pdldata.person.forelderBarnRelasjon.relatertPersonsRolle", "BARN"));
        }
    }

    public static void addForeldreQuery(Criteria criteria, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarForeldre())) {
            criteria.and(getCriteria("pdldata.person.forelderBarnRelasjon.minRolleForPerson", "BARN"));
        }
    }

    public static void addSivilstandQuery(Criteria criteria, SearchRequest request) {

        if (nonNull(request.getPersonRequest().getSivilstand())) {
            criteria.and(getCriteria("pdldata.person.sivilstand.type",
                    request.getPersonRequest().getSivilstand().name()));
        }
    }

    public static void addHarDoedfoedtbarnQuery(Criteria criteria, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarDoedfoedtBarn())) {
            criteria.and(new Criteria("pdldata.person.doedfoedtBarn").exists());
        }
    }

    public static void addHarForeldreansvarQuery(Criteria criteria, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarForeldreAnsvar())) {
            criteria.and(new Criteria("pdldata.person.foreldreansvar").exists());
        }
    }

    public static void addVergemaalQuery(Criteria criteria, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarVerge())) {
            criteria.and(new Criteria("pdldata.person.vergemaal").exists());
        }
    }

    public static void addDoedsfallQuery(Criteria criteria, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarDoedsfall())) {
            criteria.and(new Criteria("pdldata.person.doedsfall").exists());
        }
    }

    public static void addFullmaktQuery(Criteria criteria, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarFullmakt())) {
            criteria.and(new Criteria("pdldata.person.fullmakt").exists());
        }
    }

    public static void addHarInnflyttingQuery(Criteria criteria, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarInnflytting())) {
            criteria.and(new Criteria("pdldata.person.innflytting").exists());
        }
    }

    public static void addHarUtflyttingQuery(Criteria criteria, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarUtflytting())) {
            criteria.and(new Criteria("pdldata.person.utflytting").exists());
        }
    }

    public static void addAdressebeskyttelseQuery(Criteria criteria, SearchRequest request) {

        if (nonNull(request.getPersonRequest().getAddressebeskyttelse())) {
            criteria.and(getCriteria("pdldata.person.adressebeskyttelse.gradering",
                    request.getPersonRequest().getAddressebeskyttelse().name()));
        }
    }

    public static void addHarOppholdsadresseQuery(Criteria criteria, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarOppholdsadresse())) {
            criteria.and(new Criteria("pdldata.person.oppholdsadresse").exists());
        }
    }

    public static void addHarKontaktadresseQuery(Criteria criteria, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarKontaktadresse())) {
            criteria.and(new Criteria("pdldata.person.kontaktadresse").exists());
        }
    }

    public static void addBostedKommuneQuery(Criteria criteria, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getBostedsadresse())
                .filter(boadresse -> isNotBlank(boadresse.getKommunenummer()))
                .ifPresent(boadresse ->
                        criteria.and(getCriteria("pdldata.person.bostedsadresse.vegadresse.kommunenummer",
                                boadresse.getKommunenummer())));
    }

    public static void addBostedPostnrQuery(Criteria criteria, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getBostedsadresse())
                .filter(boadresse -> isNotBlank(boadresse.getPostnummer()))
                .ifPresent(boadresse ->
                        criteria.and(getCriteria("pdldata.person.bostedsadresse.vegadresse.postnummer",
                                boadresse.getPostnummer())));
    }

    public static void addBostedBydelsnrQuery(Criteria criteria, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getBostedsadresse())
                .filter(boadresse -> isNotBlank(boadresse.getBydelsnummer()))
                .ifPresent(boadresse ->
                        criteria.and(getCriteria("pdldata.person.bostedsadresse.vegadresse.bydelsnummer",
                                boadresse.getBydelsnummer())));
    }

    public static void addBostedUtlandQuery(Criteria criteria, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getBostedsadresse())
                .filter(boadresse -> isTrue(boadresse.getHarUtenlandsadresse()))
                .ifPresent(boadresse ->
                        criteria.and(new Criteria("pdldata.person.bostedsadresse.utenlandskAdresse").exists()));
    }

    public static void addBostedMatrikkelQuery(Criteria criteria, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getBostedsadresse())
                .filter(boadresse -> isTrue(boadresse.getHarMatrikkelAdresse()))
                .ifPresent(boadresse ->
                        criteria.and(new Criteria("pdldata.person.bostedsadresse.matrikkeladresse").exists()));
    }

    public static void addBostedUkjentQuery(Criteria criteria, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getBostedsadresse())
                .filter(boadresse -> isTrue(boadresse.getHarUkjentAdresse()))
                .ifPresent(boadresse ->
                        criteria.and(new Criteria("pdldata.person.bostedsadresse.ukjentBosted").exists()));
    }

    public static void addHarDeltBostedQuery(Criteria criteria, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarDeltBosted())) {
            criteria.and(new Criteria("pdldata.person.forelderBarnRelasjon.deltBosted").exists());
        }
    }

    public static void addHarKontaktinformasjonForDoedsboQuery(Criteria criteria, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarKontaktinformasjonForDoedsbo())) {
            criteria.and(new Criteria("pdldata.person.kontaktinformasjonForDoedsbo").exists());
        }
    }

    public static void addHarUtenlandskIdentifikasjonsnummerQuery(Criteria criteria, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarUtenlandskIdentifikasjonsnummer())) {
            criteria.and(new Criteria("pdldata.person.utenlandskIdentifikasjonsnummer").exists());
        }
    }

    public static void addHarFalskIdentitetQuery(Criteria criteria, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarFalskIdentitet())) {
            criteria.and(new Criteria("pdldata.person.falskIdentitet").exists());
        }
    }

    public static void addHarTilrettelagtKommunikasjonQuery(Criteria criteria, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarTilrettelagtKommunikasjon())) {
            criteria.and(new Criteria("pdldata.person.tilrettelagtKommunikasjon").exists());
        }
    }

    public static void addHarSikkerhetstiltakQuery(Criteria criteria, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarSikkerhetstiltak())) {
            criteria.and(new Criteria("pdldata.person.sikkerhetstiltak").exists());
        }
    }

    public static void addStatsborgerskapQuery(Criteria criteria, SearchRequest request) {

        if (isNotBlank(request.getPersonRequest().getStatsborgerskap())) {
            criteria.and(new Criteria("pdldata.person.statsborgerskap.landkode")
                    .is(request.getPersonRequest().getStatsborgerskap()));
        }
    }

    public static void addHarOppholdQuery(Criteria criteria, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarOpphold())) {
            criteria.and(new Criteria("pdldata.person.opphold").exists());
        }
    }

    public static void addHarNyIdentitetQuery(Criteria criteria, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getNyIdentitet())) {
            criteria.and(new Criteria("pdldata.person.nyident").exists());
        }
    }

    public static void addKjoennQuery(Criteria criteria, SearchRequest request) {

        if (nonNull(request.getPersonRequest().getKjoenn())) {
            criteria.and(getCriteria("pdldata.person.kjoenn.kjoenn",
                    request.getPersonRequest().getKjoenn().name()));
        }
    }

    public static void addIdenttypeQuery(Criteria criteria, SearchRequest request) {

        if (nonNull(request.getPersonRequest().getIdenttype())) {
            criteria.subCriteria(getCriteria("pdldata.opprettNyPerson.identtype",
                    request.getPersonRequest().getIdenttype().name())
                    .or(getCriteria("pdldata.person.nyident.identtype",
                            request.getPersonRequest().getIdenttype().name())));
        }
    }

    private Criteria getCriteria(String field, String value) {

        return new Criteria(field).is(value);
    }
}