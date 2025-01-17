package no.nav.testnav.dollysearchservice.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import org.apache.lucene.search.join.ScoreMode;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class OpenSearchPersonQueryUtils {

    private static final String FAMILIE_RELASJON_PATH = "hentPerson.forelderBarnRelasjon";
    private static final String FOLKEREGISTER_METADATA_FIELD = "folkeregistermetadata";

    public static void addAlderQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        var thisYear = LocalDate.now().getYear();
        if (nonNull(request.getPersonRequest().getAlderFom()) || nonNull(request.getPersonRequest().getAlderTom())) {
            queryBuilder.must(QueryBuilders.nestedQuery("hentPerson.foedselsdato",
                    QueryBuilders.boolQuery().must(
                            rangeQuery("hentPerson.foedselsdato.foedselsaar",
                                    Optional.ofNullable(request.getPersonRequest().getAlderTom())
                                            .map(alderTom -> thisYear - alderTom)
                                            .orElse(null),
                                    Optional.ofNullable(request.getPersonRequest().getAlderFom())
                                            .map(alderFom -> thisYear - alderFom)
                                            .orElse(null))), ScoreMode.Avg));
        }
    }

    public static void addHarBarnQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarBarn())) {
            queryBuilder.must(nestedMatchQuery(FAMILIE_RELASJON_PATH, "relatertPersonsRolle", "BARN"));
        }
    }

    public static void addHarForeldreQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarForeldre())) {
            queryBuilder.must(nestedMatchQuery(FAMILIE_RELASJON_PATH, "minRolleForPerson", "BARN"));
        }
    }

    public static void addSivilstandQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (nonNull(request.getPersonRequest().getSivilstand())) {
            queryBuilder.must(nestedMatchQuery("hentPerson.sivilstand","type",
                            request.getPersonRequest().getSivilstand().name()));
        }
    }

    public static void addHarDoedfoedtbarnQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarDoedfoedtBarn())) {
            queryBuilder.must(nestedExistQuery("hentPerson.doedfoedtBarn", FOLKEREGISTER_METADATA_FIELD));
        }
    }

    public static void addHarForeldreansvarQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarForeldreAnsvar())) {
            queryBuilder.must(nestedExistQuery("hentPerson.foreldreansvar", FOLKEREGISTER_METADATA_FIELD));
        }
    }

    public static void addVergemaalQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarVerge())) {
            queryBuilder.must(nestedExistQuery("hentPerson.vergemaalEllerFremtidsfullmakt", FOLKEREGISTER_METADATA_FIELD));
        }
    }

    public static void addDoedsfallQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarDoedsfall())) {
            queryBuilder.must(nestedExistQuery("hentPerson.doedsfall", "doedsdato"));
        }
    }

    public static void addHarInnflyttingQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarInnflytting())) {
            queryBuilder.must(nestedExistQuery("hentPerson.innflyttingTilNorge", FOLKEREGISTER_METADATA_FIELD));
        }
    }

    public static void addHarUtflyttingQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarUtflytting())) {
            queryBuilder.must(nestedExistQuery("hentPerson.utflyttingFraNorge", FOLKEREGISTER_METADATA_FIELD));
        }
    }

    public static void addAdressebeskyttelseQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (nonNull(request.getPersonRequest().getAddressebeskyttelse())) {
            queryBuilder.must(nestedMatchQuery("hentPerson.adressebeskyttelse","gradering",
                    request.getPersonRequest().getAddressebeskyttelse().name()));
        }
    }

    public static void addHarOppholdsadresseQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarOppholdsadresse())) {
            queryBuilder.must(existQuery("hentPerson.oppholdsadresse"));
        }
    }

    public static void addHarKontaktadresseQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarKontaktadresse())) {
            queryBuilder.must(existQuery("hentPerson.kontaktadresse"));
        }
    }

    public static void addBostedKommuneQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getBostedsadresse())
                .filter(boadresse -> isNotBlank(boadresse.getKommunenummer()))
                .ifPresent(boadresse ->
                        queryBuilder.must(QueryBuilders.nestedQuery("hentPerson.bostedsadresse.vegadresse",
                                QueryBuilders.boolQuery().must(matchQuery("hentPerson.bostedsadresse.vegadresse.kommunenummer",
                                        boadresse.getKommunenummer())), ScoreMode.Avg)));
    }

    public static void addBostedPostnrQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getBostedsadresse())
                .filter(boadresse -> isNotBlank(boadresse.getPostnummer()))
                .ifPresent(boadresse ->
                        queryBuilder.must(QueryBuilders.nestedQuery("hentPerson.bostedsadresse.vegadresse",
                                QueryBuilders.boolQuery().must(matchQuery("hentPerson.bostedsadresse.vegadresse.postnummer",
                                        boadresse.getPostnummer())), ScoreMode.Avg)));
    }

    public static void addBostedBydelsnrQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getBostedsadresse())
                .filter(boadresse -> isNotBlank(boadresse.getBydelsnummer()))
                .ifPresent(boadresse ->
                        queryBuilder.must(QueryBuilders.nestedQuery("hentPerson.bostedsadresse.vegadresse",
                                QueryBuilders.boolQuery().must(matchQuery("hentPerson.bostedsadresse.vegadresse.bydelsnummer",
                                        boadresse.getBydelsnummer())), ScoreMode.Avg)));
    }

    public static void addHarBostedBydelsnrQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getBostedsadresse())
                .filter(boadresse -> isTrue(boadresse.getHarBydelsnummer()))
                .ifPresent(boadresse ->
                        queryBuilder.must(QueryBuilders.nestedQuery("hentPerson.bostedsadresse.vegadresse",
                                QueryBuilders.boolQuery().must(existQuery("hentPerson.bostedsadresse.vegadresse.bydelsnummer")), ScoreMode.Avg)));
    }

    public static void addBostedUtlandQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getBostedsadresse())
                .filter(boadresse -> isTrue(boadresse.getHarUtenlandsadresse()))
                .ifPresent(boadresse ->
                        queryBuilder.must(QueryBuilders.nestedQuery("hentPerson.bostedsadresse",
                                QueryBuilders.boolQuery().must(existQuery("hentPerson.bostedsadresse.utenlandskAdresse")), ScoreMode.Avg)));
    }

    public static void addBostedMatrikkelQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getBostedsadresse())
                .filter(boadresse -> isTrue(boadresse.getHarMatrikkelAdresse()))
                .ifPresent(boadresse ->
                        queryBuilder.must(QueryBuilders.nestedQuery("hentPerson.bostedsadresse",
                                QueryBuilders.boolQuery().must(existQuery("hentPerson.bostedsadresse.matrikkeladresse")), ScoreMode.Avg)));
    }

    public static void addBostedUkjentQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getBostedsadresse())
                .filter(boadresse -> isTrue(boadresse.getHarUkjentAdresse()))
                .ifPresent(boadresse ->
                        queryBuilder.must(QueryBuilders.nestedQuery("hentPerson.bostedsadresse",
                                QueryBuilders.boolQuery().must(
                                        existQuery("hentPerson.bostedsadresse.ukjentBosted")), ScoreMode.Avg)));
    }

    public static void addHarDeltBostedQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarDeltBosted())) {
            queryBuilder.must(nestedExistQuery("hentPerson.deltBosted","startdatoForKontrakt"));
        }
    }

    public static void addHarKontaktinformasjonForDoedsboQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarKontaktinformasjonForDoedsbo())) {
            queryBuilder.must(nestedExistQuery("hentPerson.kontaktinformasjonForDoedsbo", FOLKEREGISTER_METADATA_FIELD));
        }
    }

    public static void addHarUtenlandskIdentifikasjonsnummerQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarUtenlandskIdentifikasjonsnummer())) {
            queryBuilder.must(nestedExistQuery("hentPerson.utenlandskIdentifikasjonsnummer", "identifikasjonsnummer"));
        }
    }

    public static void addHarFalskIdentitetQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarFalskIdentitet())) {
            queryBuilder.must(nestedExistQuery("hentPerson.falskIdentitet", FOLKEREGISTER_METADATA_FIELD));
        }
    }

    public static void addHarTilrettelagtKommunikasjonQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarTilrettelagtKommunikasjon())) {
            queryBuilder.must(nestedExistQuery("hentPerson.tilrettelagtKommunikasjon", "metadata"));
        }
    }

    public static void addHarSikkerhetstiltakQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarSikkerhetstiltak())) {
            queryBuilder.must(nestedExistQuery("hentPerson.sikkerhetstiltak", "tiltakstype"));
        }
    }

    public static void addStatsborgerskapQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isNotBlank(request.getPersonRequest().getStatsborgerskap())) {
            queryBuilder.must(nestedMatchQuery("hentPerson.statsborgerskap", "land",
                    request.getPersonRequest().getStatsborgerskap()));
        }
    }

    public static void addHarOppholdQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarOpphold())) {
            queryBuilder.must(nestedExistQuery("hentPerson.opphold", "type"));
        }
    }

    public static void addHarNyIdentitetQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarNyIdentitet())) {
            queryBuilder.must(existQuery("hentPerson.nyident"));
        }
    }

    public static void addKjoennQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (nonNull(request.getPersonRequest().getKjoenn())) {
            queryBuilder.must(nestedMatchQuery("hentPerson.kjoenn", "kjoenn",
                            request.getPersonRequest().getKjoenn().name()));
        }
    }

    public static void addIdenttypeQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (nonNull(request.getPersonRequest().getIdenttype())) {
            queryBuilder.must(QueryBuilders.boolQuery()
                    .should(matchQuery("pdldata.opprettNyPerson.identtype",
                            request.getPersonRequest().getIdenttype().name()))
                    .should(matchQuery("hentPerson.nyident.identtype",
                            request.getPersonRequest().getIdenttype().name()))
                    .minimumShouldMatch(1));
        }
    }

    private QueryBuilder rangeQuery(String field, Integer value1, Integer value2) {

        return QueryBuilders.rangeQuery(field).from(value1).to(value2);
    }

    private QueryBuilder matchQuery(String field, String value) {

        return QueryBuilders.matchQuery(field, value);
    }

    private QueryBuilder existQuery(String field) {

        return QueryBuilders.existsQuery(field);
    }

    private QueryBuilder nestedMatchQuery(String path, String field, String value) {

        return QueryBuilders.nestedQuery(path, matchQuery(path + '.' + field, value), ScoreMode.Avg);
    }

    private QueryBuilder nestedShouldQuery(String path, Map<String, String> fieldValues) {

        var boolQuery = QueryBuilders.boolQuery();
        fieldValues.forEach((field, value) -> boolQuery.should(matchQuery(path + '.' + field, value)));
        return QueryBuilders.nestedQuery(path, boolQuery, ScoreMode.Avg);
    }

    private QueryBuilder nestedExistQuery(String path, String field) {

        return QueryBuilders.nestedQuery(path, existQuery(path + '.' + field), ScoreMode.Avg);
    }
}
