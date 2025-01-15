package no.nav.testnav.dollysearchservice.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

import java.util.Optional;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class OpenSearchPersonQueryUtils {

    public static void addBarnQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarBarn())) {
            queryBuilder.must(matchQuery("pdldata.person.forelderBarnRelasjon.relatertPersonsRolle", "BARN"));
        }
    }

    public static void addForeldreQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarForeldre())) {
            queryBuilder.must(matchQuery("pdldata.person.forelderBarnRelasjon.minRolleForPerson", "BARN"));
        }
    }

    public static void addSivilstandQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (nonNull(request.getPersonRequest().getSivilstand())) {
            queryBuilder.must(matchQuery("pdldata.person.sivilstand.type",
                    request.getPersonRequest().getSivilstand().name()));
        }
    }

    public static void addHarDoedfoedtbarnQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarDoedfoedtBarn())) {
            queryBuilder.must(existQuery("pdldata.person.doedfoedtBarn"));
        }
    }

    public static void addHarForeldreansvarQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarForeldreAnsvar())) {
            queryBuilder.must(existQuery("pdldata.person.foreldreansvar"));
        }
    }

    public static void addVergemaalQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarVerge())) {
            queryBuilder.must(existQuery("pdldata.person.vergemaal"));
        }
    }

    public static void addDoedsfallQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarDoedsfall())) {
            queryBuilder.must(existQuery("pdldata.person.doedsfall"));
        }
    }

    public static void addHarInnflyttingQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarInnflytting())) {
            queryBuilder.must(existQuery("pdldata.person.innflytting"));
        }
    }

    public static void addHarUtflyttingQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarUtflytting())) {
            queryBuilder.must(existQuery("pdldata.person.utflytting"));
        }
    }

    public static void addAdressebeskyttelseQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (nonNull(request.getPersonRequest().getAddressebeskyttelse())) {
            queryBuilder.must(matchQuery("pdldata.person.adressebeskyttelse.gradering",
                    request.getPersonRequest().getAddressebeskyttelse().name()));
        }
    }

    public static void addHarOppholdsadresseQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarOppholdsadresse())) {
            queryBuilder.must(existQuery("pdldata.person.oppholdsadresse"));
        }
    }

    public static void addHarKontaktadresseQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarKontaktadresse())) {
            queryBuilder.must(existQuery("pdldata.person.kontaktadresse"));
        }
    }

    public static void addBostedKommuneQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getBostedsadresse())
                .filter(boadresse -> isNotBlank(boadresse.getKommunenummer()))
                .ifPresent(boadresse ->
                        queryBuilder.must(matchQuery("pdldata.person.bostedsadresse.vegadresse.kommunenummer",
                                boadresse.getKommunenummer())));
    }

    public static void addBostedPostnrQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getBostedsadresse())
                .filter(boadresse -> isNotBlank(boadresse.getPostnummer()))
                .ifPresent(boadresse ->
                        queryBuilder.must(matchQuery("pdldata.person.bostedsadresse.vegadresse.postnummer",
                                boadresse.getPostnummer())));
    }

    public static void addBostedBydelsnrQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getBostedsadresse())
                .filter(boadresse -> isNotBlank(boadresse.getBydelsnummer()))
                .ifPresent(boadresse ->
                        queryBuilder.must(matchQuery("pdldata.person.bostedsadresse.vegadresse.bydelsnummer",
                                boadresse.getBydelsnummer())));
    }

    public static void addHarBostedBydelsnrQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getBostedsadresse())
                .filter(boadresse -> isTrue(boadresse.getHarBydelsnummer()))
                .ifPresent(boadresse ->
                        queryBuilder.must(existQuery("pdldata.person.bostedsadresse.vegadresse.bydelsnummer")));
    }

    public static void addBostedUtlandQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getBostedsadresse())
                .filter(boadresse -> isTrue(boadresse.getHarUtenlandsadresse()))
                .ifPresent(boadresse ->
                        queryBuilder.must(existQuery("pdldata.person.bostedsadresse.utenlandskAdresse")));
    }

    public static void addBostedMatrikkelQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getBostedsadresse())
                .filter(boadresse -> isTrue(boadresse.getHarMatrikkelAdresse()))
                .ifPresent(boadresse ->
                        queryBuilder.must(existQuery("pdldata.person.bostedsadresse.matrikkeladresse")));
    }

    public static void addBostedUkjentQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getBostedsadresse())
                .filter(boadresse -> isTrue(boadresse.getHarUkjentAdresse()))
                .ifPresent(boadresse ->
                        queryBuilder.must(existQuery("pdldata.person.bostedsadresse.ukjentBosted")));
    }

    public static void addHarDeltBostedQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarDeltBosted())) {
            queryBuilder.must(existQuery("pdldata.person.forelderBarnRelasjon.deltBosted"));
        }
    }

    public static void addHarKontaktinformasjonForDoedsboQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarKontaktinformasjonForDoedsbo())) {
            queryBuilder.must(existQuery("pdldata.person.kontaktinformasjonForDoedsbo"));
        }
    }

    public static void addHarUtenlandskIdentifikasjonsnummerQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarUtenlandskIdentifikasjonsnummer())) {
            queryBuilder.must(existQuery("pdldata.person.utenlandskIdentifikasjonsnummer"));
        }
    }

    public static void addHarFalskIdentitetQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarFalskIdentitet())) {
            queryBuilder.must(existQuery("pdldata.person.falskIdentitet"));
        }
    }

    public static void addHarTilrettelagtKommunikasjonQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarTilrettelagtKommunikasjon())) {
            queryBuilder.must(existQuery("pdldata.person.tilrettelagtKommunikasjon"));
        }
    }

    public static void addHarSikkerhetstiltakQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarSikkerhetstiltak())) {
            queryBuilder.must(existQuery("pdldata.person.sikkerhetstiltak"));
        }
    }

    public static void addStatsborgerskapQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isNotBlank(request.getPersonRequest().getStatsborgerskap())) {
            queryBuilder.must(matchQuery("pdldata.person.statsborgerskap.landkode",
                    request.getPersonRequest().getStatsborgerskap()));
        }
    }

    public static void addHarOppholdQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarOpphold())) {
            queryBuilder.must(existQuery("pdldata.person.opphold"));
        }
    }

    public static void addHarNyIdentitetQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarNyIdentitet())) {
            queryBuilder.must(existQuery("pdldata.person.nyident"));
        }
    }

    public static void addKjoennQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (nonNull(request.getPersonRequest().getKjoenn())) {
            queryBuilder.must(matchQuery("pdldata.person.kjoenn.kjoenn",
                    request.getPersonRequest().getKjoenn().name()));
        }
    }

    public static void addIdenttypeQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (nonNull(request.getPersonRequest().getIdenttype())) {
            queryBuilder.must(QueryBuilders.boolQuery()
                    .should(matchQuery("pdldata.opprettNyPerson.identtype",
                            request.getPersonRequest().getIdenttype().name()))
                    .should(matchQuery("pdldata.person.nyident.identtype",
                            request.getPersonRequest().getIdenttype().name()))
                    .minimumShouldMatch(1));
        }
    }

    private QueryBuilder matchQuery(String field, String value) {

        return QueryBuilders.matchQuery(field, value);
    }

    private QueryBuilder existQuery(String field) {

        return QueryBuilders.existsQuery(field);
    }
}
