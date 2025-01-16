package no.nav.testnav.dollysearchservice.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

import java.time.LocalDate;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class OpenSearchPersonQueryUtils {

    public static void addAlderQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        var thisYear = LocalDate.now().getYear();
        if (nonNull(request.getPersonRequest().getAlderFom()) || nonNull(request.getPersonRequest().getAlderTom())) {
            queryBuilder.must(rangeQuery("hentPerson.foedselsdato.foedselsaar",
                    thisYear - request.getPersonRequest().getAlderTom(),
                    thisYear - request.getPersonRequest().getAlderFom()));
        }
    }
    
    public static void addBarnQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarBarn())) {
            queryBuilder.must(matchQuery("hentPerson.forelderBarnRelasjon.relatertPersonsRolle", "BARN"));
        }
    }

    public static void addForeldreQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarForeldre())) {
            queryBuilder.must(matchQuery("hentPerson.forelderBarnRelasjon.minRolleForPerson", "BARN"));
        }
    }

    public static void addSivilstandQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (nonNull(request.getPersonRequest().getSivilstand())) {
            queryBuilder.must(matchQuery("hentPerson.sivilstand.type",
                    request.getPersonRequest().getSivilstand().name()));
        }
    }

    public static void addHarDoedfoedtbarnQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarDoedfoedtBarn())) {
            queryBuilder.must(existQuery("hentPerson.doedfoedtBarn"));
        }
    }

    public static void addHarForeldreansvarQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarForeldreAnsvar())) {
            queryBuilder.must(existQuery("hentPerson.foreldreansvar"));
        }
    }

    public static void addVergemaalQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarVerge())) {
            queryBuilder.must(existQuery("hentPerson.vergemaal"));
        }
    }

    public static void addDoedsfallQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarDoedsfall())) {
            queryBuilder.must(existQuery("hentPerson.doedsfall"));
        }
    }

    public static void addHarInnflyttingQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarInnflytting())) {
            queryBuilder.must(existQuery("hentPerson.innflytting"));
        }
    }

    public static void addHarUtflyttingQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarUtflytting())) {
            queryBuilder.must(existQuery("hentPerson.utflytting"));
        }
    }

    public static void addAdressebeskyttelseQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (nonNull(request.getPersonRequest().getAddressebeskyttelse())) {
            queryBuilder.must(matchQuery("hentPerson.adressebeskyttelse.gradering",
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
                        queryBuilder.must(matchQuery("hentPerson.bostedsadresse.vegadresse.kommunenummer",
                                boadresse.getKommunenummer())));
    }

    public static void addBostedPostnrQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getBostedsadresse())
                .filter(boadresse -> isNotBlank(boadresse.getPostnummer()))
                .ifPresent(boadresse ->
                        queryBuilder.must(matchQuery("hentPerson.bostedsadresse.vegadresse.postnummer",
                                boadresse.getPostnummer())));
    }

    public static void addBostedBydelsnrQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getBostedsadresse())
                .filter(boadresse -> isNotBlank(boadresse.getBydelsnummer()))
                .ifPresent(boadresse ->
                        queryBuilder.must(matchQuery("hentPerson.bostedsadresse.vegadresse.bydelsnummer",
                                boadresse.getBydelsnummer())));
    }

    public static void addHarBostedBydelsnrQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getBostedsadresse())
                .filter(boadresse -> isTrue(boadresse.getHarBydelsnummer()))
                .ifPresent(boadresse ->
                        queryBuilder.must(existQuery("hentPerson.bostedsadresse.vegadresse.bydelsnummer")));
    }

    public static void addBostedUtlandQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getBostedsadresse())
                .filter(boadresse -> isTrue(boadresse.getHarUtenlandsadresse()))
                .ifPresent(boadresse ->
                        queryBuilder.must(existQuery("hentPerson.bostedsadresse.utenlandskAdresse")));
    }

    public static void addBostedMatrikkelQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getBostedsadresse())
                .filter(boadresse -> isTrue(boadresse.getHarMatrikkelAdresse()))
                .ifPresent(boadresse ->
                        queryBuilder.must(existQuery("hentPerson.bostedsadresse.matrikkeladresse")));
    }

    public static void addBostedUkjentQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getBostedsadresse())
                .filter(boadresse -> isTrue(boadresse.getHarUkjentAdresse()))
                .ifPresent(boadresse ->
                        queryBuilder.must(existQuery("hentPerson.bostedsadresse.ukjentBosted")));
    }

    public static void addHarDeltBostedQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarDeltBosted())) {
            queryBuilder.must(existQuery("hentPerson.forelderBarnRelasjon.deltBosted"));
        }
    }

    public static void addHarKontaktinformasjonForDoedsboQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarKontaktinformasjonForDoedsbo())) {
            queryBuilder.must(existQuery("hentPerson.kontaktinformasjonForDoedsbo"));
        }
    }

    public static void addHarUtenlandskIdentifikasjonsnummerQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarUtenlandskIdentifikasjonsnummer())) {
            queryBuilder.must(existQuery("hentPerson.utenlandskIdentifikasjonsnummer"));
        }
    }

    public static void addHarFalskIdentitetQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarFalskIdentitet())) {
            queryBuilder.must(existQuery("hentPerson.falskIdentitet"));
        }
    }

    public static void addHarTilrettelagtKommunikasjonQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarTilrettelagtKommunikasjon())) {
            queryBuilder.must(existQuery("hentPerson.tilrettelagtKommunikasjon"));
        }
    }

    public static void addHarSikkerhetstiltakQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarSikkerhetstiltak())) {
            queryBuilder.must(existQuery("hentPerson.sikkerhetstiltak"));
        }
    }

    public static void addStatsborgerskapQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isNotBlank(request.getPersonRequest().getStatsborgerskap())) {
            queryBuilder.must(matchQuery("hentPerson.statsborgerskap.landkode",
                    request.getPersonRequest().getStatsborgerskap()));
        }
    }

    public static void addHarOppholdQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarOpphold())) {
            queryBuilder.must(existQuery("hentPerson.opphold"));
        }
    }

    public static void addHarNyIdentitetQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarNyIdentitet())) {
            queryBuilder.must(existQuery("hentPerson.nyident"));
        }
    }

    public static void addKjoennQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (nonNull(request.getPersonRequest().getKjoenn())) {
            queryBuilder.must(matchQuery("hentPerson.kjoenn.kjoenn",
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

        var range = QueryBuilders.rangeQuery(field);
        if (nonNull(value1)) {
            range.gte(value1);
        }
        if (nonNull(value2)) {
            range.lte(value2);
        }
        return range;
    }

    private QueryBuilder matchQuery(String field, String value) {

        return QueryBuilders.matchQuery(field, value);
    }

    private QueryBuilder existQuery(String field) {

        return QueryBuilders.existsQuery(field);
    }
}
