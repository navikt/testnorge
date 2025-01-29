package no.nav.testnav.dollysearchservice.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import no.nav.testnav.libs.data.pdlforvalter.v1.Identtype;
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
    private static final String HENT_IDENTER = "hentIdenter.identer";
    private static final String HISTORISK = "historisk";
    private static final String METADATA_HISTORISK = "metadata.historisk";
    private static final String BOSTEDSADRESSE = "hentPerson.bostedsadresse";
    private static final String OPPHOLDSADRESSE = "hentPerson.oppholdsadresse";
    private static final String KONTAKTADRESSE = "hentPerson.kontaktadresse";
    private static final String VEGADRESSE = "vegadresse";
    private static final String MATRIKKELADRESSE = "matrikkeladresse";
    private static final String UTENLANDSKADRESSE = "utenlandskAdresse";
    private static final String KOMMUNENUMMER = "kommunenummer";
    private static final String POSTNUMMER = "postnummer";
    private static final String BYDELSNUMMER = "bydelsnummer";
    private static final String FOLKEREGISTERIDENTIFIKATOR = "hentPerson.folkeregisteridentifikator";
    private static final String NAVSPERSONIDENTIFIKATOR = "hentPerson.navspersonidentifikator";
    private static final String CONCAT = "%s.%s";

    public static BoolQueryBuilder addDollyIdentifier() {

        return QueryBuilders.boolQuery()
                .should(matchQuery("tags", "DOLLY"))
                .should(QueryBuilders.boolQuery()
                        .must(nestedMatchQuery(FOLKEREGISTERIDENTIFIKATOR, METADATA_HISTORISK, false))
                        .must(nestedRegexpQuery(FOLKEREGISTERIDENTIFIKATOR, "identifikasjonsnummer", "\\d{2}[4-5]\\d{8}")))
                .should(QueryBuilders.boolQuery()
                        .must(nestedMatchQuery(NAVSPERSONIDENTIFIKATOR, METADATA_HISTORISK, false))
                        .must(nestedRegexpQuery(NAVSPERSONIDENTIFIKATOR, "identifikasjonsnummer", "\\d{2}[6-7]\\d{8}")));
    }

    public static void addAlderQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        var thisYear = LocalDate.now().getYear();
        if (nonNull(request.getPersonRequest().getAlderFom()) || nonNull(request.getPersonRequest().getAlderTom())) {
            queryBuilder.must(QueryBuilders.boolQuery()
                    .must(nestedMatchQuery("hentPerson.foedselsdato", METADATA_HISTORISK, false))
                    .must(QueryBuilders.nestedQuery("hentPerson.foedselsdato",
                            QueryBuilders.boolQuery().must(
                                    rangeQuery("hentPerson.foedselsdato.foedselsaar",
                                            Optional.ofNullable(request.getPersonRequest().getAlderTom())
                                                    .map(alderTom -> thisYear - alderTom)
                                                    .orElse(null),
                                            Optional.ofNullable(request.getPersonRequest().getAlderFom())
                                                    .map(alderFom -> thisYear - alderFom)
                                                    .orElse(null))), ScoreMode.Avg)));
        }
    }

    public static void addHarBarnQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarBarn())) {
            queryBuilder.must(QueryBuilders.boolQuery()
                    .must(nestedMatchQuery(FAMILIE_RELASJON_PATH, METADATA_HISTORISK, false))
                    .must(nestedMatchQuery(FAMILIE_RELASJON_PATH, "relatertPersonsRolle", "BARN"))
            );
        }
    }

    public static void addHarForeldreQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarForeldre())) {
            queryBuilder.must(QueryBuilders.boolQuery()
                    .must(nestedMatchQuery(FAMILIE_RELASJON_PATH, METADATA_HISTORISK, false))
                    .must(nestedMatchQuery(FAMILIE_RELASJON_PATH, "minRolleForPerson", "BARN"))
            );
        }
    }

    public static void addSivilstandQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (nonNull(request.getPersonRequest().getSivilstand())) {
            queryBuilder.must(QueryBuilders.boolQuery()
                    .must(nestedMatchQuery("hentPerson.sivilstand", METADATA_HISTORISK, false))
                    .must(nestedMatchQuery("hentPerson.sivilstand", "type",
                            request.getPersonRequest().getSivilstand().name())
                    ));
        }
    }

    public static void addHarDoedfoedtbarnQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarDoedfoedtBarn())) {
            queryBuilder.must(QueryBuilders.boolQuery()
                    .must(nestedMatchQuery("hentPerson.doedfoedtBarn", METADATA_HISTORISK, false)));
        }
    }

    public static void addHarForeldreansvarQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarForeldreAnsvar())) {
            queryBuilder.must(QueryBuilders.boolQuery()
                    .must(nestedMatchQuery("hentPerson.foreldreansvar", METADATA_HISTORISK, false)));
        }
    }

    public static void addVergemaalQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarVerge())) {
            queryBuilder.must(QueryBuilders.boolQuery()
                    .must(nestedMatchQuery("hentPerson.vergemaalEllerFremtidsfullmakt", METADATA_HISTORISK, false)));
        }
    }

    public static void addDoedsfallQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarDoedsfall())) {
            queryBuilder.must(QueryBuilders.boolQuery()
                    .must(nestedMatchQuery("hentPerson.doedsfall", METADATA_HISTORISK, false)));
        }
    }

    public static void addHarInnflyttingQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarInnflytting())) {
            queryBuilder.must(QueryBuilders.boolQuery()
                    .must(nestedMatchQuery("hentPerson.innflyttingTilNorge", METADATA_HISTORISK, false)));
        }
    }

    public static void addHarUtflyttingQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarUtflytting())) {
            queryBuilder.must(QueryBuilders.boolQuery()
                    .must(nestedMatchQuery("hentPerson.utflyttingFraNorge", METADATA_HISTORISK, false)));
        }
    }

    public static void addAdressebeskyttelseQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getAdresse())
                .filter(adresse -> nonNull(adresse.getAddressebeskyttelse()))
                .ifPresent(adresse ->
                        queryBuilder.must(QueryBuilders.boolQuery()
                                .must(nestedMatchQuery("hentPerson.adressebeskyttelse", METADATA_HISTORISK, false))
                                .must(nestedMatchQuery("hentPerson.adressebeskyttelse", "gradering",
                                        adresse.getAddressebeskyttelse().name()))
                        ));
    }

    public static void addHarBostedsadresseQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getAdresse())
                .filter(adresse -> isTrue(adresse.getHarBostedsadresse()))
                .ifPresent(adresse ->
                        queryBuilder.must(nestedMatchQuery(BOSTEDSADRESSE, METADATA_HISTORISK, false))
                );
    }

    public static void addHarOppholdsadresseQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getAdresse())
                .filter(adresse -> isTrue(adresse.getHarOppholdsadresse()))
                .ifPresent(adresse ->
                        queryBuilder.must(nestedMatchQuery(OPPHOLDSADRESSE, METADATA_HISTORISK, false))
                );
    }

    public static void addHarKontaktadresseQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getAdresse())
                .filter(adresse -> isTrue(adresse.getHarKontaktadresse()))
                .ifPresent(adresse ->
                        queryBuilder.must(nestedMatchQuery(KONTAKTADRESSE, METADATA_HISTORISK, false))
                );
    }

    public static BoolQueryBuilder addAdresseQuery(String field, String value) {

        return QueryBuilders.boolQuery()
                .should(QueryBuilders.boolQuery()
                        .must(nestedMatchQuery(BOSTEDSADRESSE, METADATA_HISTORISK, false))
                        .must(QueryBuilders.boolQuery()
                                .should(nestedMatchQuery(BOSTEDSADRESSE, CONCAT.formatted(VEGADRESSE, field), value))
                                .should(nestedMatchQuery(BOSTEDSADRESSE, CONCAT.formatted(MATRIKKELADRESSE, field), value))
                        ))
                .should(QueryBuilders.boolQuery()
                        .must(nestedMatchQuery(OPPHOLDSADRESSE, METADATA_HISTORISK, false))
                        .must(QueryBuilders.boolQuery()
                                .should(nestedMatchQuery(OPPHOLDSADRESSE, CONCAT.formatted(VEGADRESSE, field), value))
                                .should(nestedMatchQuery(OPPHOLDSADRESSE, CONCAT.formatted(MATRIKKELADRESSE, field), value))
                        ))
                .should(QueryBuilders.boolQuery()
                        .must(nestedMatchQuery(KONTAKTADRESSE, METADATA_HISTORISK, false))
                        .must(nestedMatchQuery(KONTAKTADRESSE, CONCAT.formatted(VEGADRESSE, field), value))
                );
    }

    public static void addAdresseKommunenrQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getAdresse())
                .filter(adresse -> isNotBlank(adresse.getKommunenummer()))
                .ifPresent(adresse ->
                        queryBuilder.must(QueryBuilders.boolQuery()
                                .should(addAdresseQuery(KOMMUNENUMMER, adresse.getKommunenummer()))
                                .should(QueryBuilders.boolQuery()
                                        .must(nestedMatchQuery(BOSTEDSADRESSE, METADATA_HISTORISK, false))
                                        .must(nestedMatchQuery(BOSTEDSADRESSE, "ukjentBosted.bostedskommune", adresse.getKommunenummer()))
                                )));
    }

    public static void addAdressePostnrQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getAdresse())
                .filter(adresse -> isNotBlank(adresse.getPostnummer()))
                .ifPresent(adresse ->
                        queryBuilder.must(QueryBuilders.boolQuery()
                                .should(addAdresseQuery(POSTNUMMER, adresse.getPostnummer()))
                                .should(QueryBuilders.boolQuery()
                                        .must(nestedMatchQuery(KONTAKTADRESSE, METADATA_HISTORISK, false))
                                        .must(nestedMatchQuery(KONTAKTADRESSE, "postboksadresse." + POSTNUMMER, adresse.getPostnummer()))
                                )));
    }

    public static void addAdresseBydelsnrQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getAdresse())
                .filter(adresse -> isNotBlank(adresse.getBydelsnummer()))
                .ifPresent(adresse ->
                        queryBuilder.must(addAdresseQuery(BYDELSNUMMER, adresse.getBydelsnummer())
                        ));
    }

    public static void addHarAdresseBydelsnummerQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getAdresse())
                .filter(adresse -> isTrue(adresse.getHarBydelsnummer()))
                .ifPresent(adresse ->
                        queryBuilder.must(QueryBuilders.boolQuery()
                                .should(QueryBuilders.boolQuery()
                                        .must(nestedMatchQuery(BOSTEDSADRESSE, METADATA_HISTORISK, false))
                                        .must(QueryBuilders.boolQuery()
                                                .should(nestedExistQuery(BOSTEDSADRESSE, CONCAT.formatted(VEGADRESSE, BYDELSNUMMER)))
                                                .should(nestedExistQuery(BOSTEDSADRESSE, CONCAT.formatted(MATRIKKELADRESSE, BYDELSNUMMER)))))
                                .should(QueryBuilders.boolQuery()
                                        .must(nestedMatchQuery(OPPHOLDSADRESSE, METADATA_HISTORISK, false))
                                        .must(QueryBuilders.boolQuery()
                                                .should(nestedExistQuery(OPPHOLDSADRESSE, CONCAT.formatted(VEGADRESSE, BYDELSNUMMER)))
                                                .should(nestedExistQuery(OPPHOLDSADRESSE, CONCAT.formatted(MATRIKKELADRESSE, BYDELSNUMMER)))))
                                .should(QueryBuilders.boolQuery()
                                        .must(nestedMatchQuery(KONTAKTADRESSE, METADATA_HISTORISK, false))
                                        .must(nestedExistQuery(KONTAKTADRESSE, CONCAT.formatted(VEGADRESSE, BYDELSNUMMER))))
                        ));
    }

    public static void addAdresseUtlandQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getAdresse())
                .filter(boadresse -> isTrue(boadresse.getHarUtenlandsadresse()))
                .ifPresent(boadresse ->
                        queryBuilder.must(QueryBuilders.boolQuery()
                                .should(QueryBuilders.boolQuery()
                                        .must(nestedExistQuery(BOSTEDSADRESSE, UTENLANDSKADRESSE))
                                        .must(nestedMatchQuery(BOSTEDSADRESSE, METADATA_HISTORISK, false)))
                                .should(QueryBuilders.boolQuery()
                                        .must(nestedExistQuery(OPPHOLDSADRESSE, UTENLANDSKADRESSE))
                                        .must(nestedMatchQuery(OPPHOLDSADRESSE, METADATA_HISTORISK, false)))
                                .should(QueryBuilders.boolQuery()
                                        .must(nestedExistQuery(KONTAKTADRESSE, UTENLANDSKADRESSE))
                                        .must(nestedMatchQuery(KONTAKTADRESSE, METADATA_HISTORISK, false))
                                )));
    }

    public static void addAdresseMatrikkelQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getAdresse())
                .filter(boadresse -> isTrue(boadresse.getHarMatrikkeladresse()))
                .ifPresent(boadresse ->
                        queryBuilder.must(QueryBuilders.boolQuery()
                                .must(nestedMatchQuery(BOSTEDSADRESSE, METADATA_HISTORISK, false))
                                .must(nestedExistQuery(BOSTEDSADRESSE, MATRIKKELADRESSE))
                        ));
    }

    public static void addHarBostedUkjentQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getAdresse())
                .filter(boadresse -> isTrue(boadresse.getHarUkjentAdresse()))
                .ifPresent(boadresse ->
                        queryBuilder.must(QueryBuilders.boolQuery()
                                .must(nestedMatchQuery(BOSTEDSADRESSE, METADATA_HISTORISK, false))
                                .must(nestedExistQuery(BOSTEDSADRESSE, "ukjentBosted"))
                        ));
    }

    public static void addHarDeltBostedQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getAdresse())
                .filter(adresse -> isTrue(adresse.getHarDeltBosted()))
                .ifPresent(adresse ->
                        queryBuilder.must(nestedMatchQuery("hentPerson.deltBosted", METADATA_HISTORISK, false))
                );
    }

    public static void addHarKontaktinformasjonForDoedsboQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarKontaktinformasjonForDoedsbo())) {
            queryBuilder.must(nestedMatchQuery("hentPerson.kontaktinformasjonForDoedsbo", METADATA_HISTORISK, false));
        }
    }

    public static void addHarUtenlandskIdentifikasjonsnummerQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarUtenlandskIdentifikasjonsnummer())) {
            queryBuilder.must(nestedMatchQuery("hentPerson.utenlandskIdentifikasjonsnummer", METADATA_HISTORISK, false));
        }
    }

    public static void addHarFalskIdentitetQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarFalskIdentitet())) {
            queryBuilder.must(nestedMatchQuery("hentPerson.falskIdentitet", METADATA_HISTORISK, false));
        }
    }

    public static void addHarTilrettelagtKommunikasjonQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarTilrettelagtKommunikasjon())) {
            queryBuilder.must(nestedMatchQuery("hentPerson.tilrettelagtKommunikasjon", METADATA_HISTORISK, false));
        }
    }

    public static void addHarSikkerhetstiltakQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarSikkerhetstiltak())) {
            queryBuilder.must(nestedMatchQuery("hentPerson.sikkerhetstiltak", METADATA_HISTORISK, false));
        }
    }

    public static void addStatsborgerskapQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isNotBlank(request.getPersonRequest().getStatsborgerskap())) {
            queryBuilder.must(QueryBuilders.boolQuery()
                    .must(nestedMatchQuery("hentPerson.statsborgerskap", METADATA_HISTORISK, false))
                    .must(nestedMatchQuery("hentPerson.statsborgerskap", "land",
                            request.getPersonRequest().getStatsborgerskap()))
            );
        }
    }

    public static void addHarOppholdQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarOpphold())) {
            queryBuilder.must(nestedMatchQuery("hentPerson.opphold", METADATA_HISTORISK, false));
        }
    }

    public static void addHarNyIdentitetQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarNyIdentitet())) {
            queryBuilder.must(QueryBuilders.boolQuery()
                    .must(nestedMatchQuery(HENT_IDENTER, HISTORISK, false))
                    .must(nestedMatchQuery(HENT_IDENTER, HISTORISK, true))
            );
        }
    }

    public static void addKjoennQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (nonNull(request.getPersonRequest().getKjoenn())) {
            queryBuilder.must(QueryBuilders.boolQuery()
                    .must(nestedMatchQuery("hentPerson.kjoenn", METADATA_HISTORISK, false))
                    .must(nestedMatchQuery("hentPerson.kjoenn", "kjoenn",
                            request.getPersonRequest().getKjoenn().name()))
            );
        }
    }

    public static void addIdenttypeQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (nonNull(request.getPersonRequest().getIdenttype())) {
            if (request.getPersonRequest().getIdenttype() == Identtype.NPID) {
                queryBuilder.must(QueryBuilders.boolQuery()
                        .should(QueryBuilders.boolQuery()
                                .must(nestedMatchQuery(NAVSPERSONIDENTIFIKATOR, METADATA_HISTORISK, false))
                                .must(nestedExistQuery(NAVSPERSONIDENTIFIKATOR, "identifikasjonsnummer"))
                        )
                        .should(QueryBuilders.boolQuery()
                                .must(nestedMatchQuery(HENT_IDENTER, HISTORISK, false))
                                .must(nestedMatchQuery(HENT_IDENTER, "gruppe", "NPID"))
                        )
                );
            } else {
                queryBuilder.must(QueryBuilders.boolQuery()
                        .must(nestedMatchQuery(FOLKEREGISTERIDENTIFIKATOR, METADATA_HISTORISK, false))
                        .must(nestedMatchQuery(FOLKEREGISTERIDENTIFIKATOR, "type",
                                request.getPersonRequest().getIdenttype().name())));
            }
        }
    }

    private QueryBuilder rangeQuery(String field, Integer value1, Integer value2) {

        return QueryBuilders.rangeQuery(field).from(value1).to(value2);
    }

    private QueryBuilder matchQuery(String field, Object value) {

        return QueryBuilders.matchQuery(field, value);
    }

    private QueryBuilder existQuery(String field) {

        return QueryBuilders.existsQuery(field);
    }

    private QueryBuilder regexpQuery(String field, String value) {

        return QueryBuilders.regexpQuery(field, value);
    }

    private QueryBuilder nestedRegexpQuery(String path, String field, String value) {

        return QueryBuilders.nestedQuery(path, regexpQuery("%s.%s".formatted(path, field), value), ScoreMode.Avg);
    }

    private QueryBuilder nestedMatchQuery(String path, String field, Object value) {

        return QueryBuilders.nestedQuery(path, matchQuery("%s.%s".formatted(path, field), value), ScoreMode.Avg);
    }

    private QueryBuilder nestedExistQuery(String path, String field) {

        return QueryBuilders.nestedQuery(path, existQuery(path + '.' + field), ScoreMode.Avg);
    }
}
