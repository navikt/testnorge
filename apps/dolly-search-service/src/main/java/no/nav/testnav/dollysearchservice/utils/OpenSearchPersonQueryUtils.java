package no.nav.testnav.dollysearchservice.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import no.nav.testnav.libs.data.pdlforvalter.v1.Identtype;
import org.apache.lucene.search.join.ScoreMode;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;

import java.time.LocalDate;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.CONCAT;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.FOLKEREGISTERIDENTIFIKATOR;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.HENT_IDENTER;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.HISTORISK;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.METADATA_HISTORISK;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.NAVSPERSONIDENTIFIKATOR;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.nestedExistQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.nestedMatchQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.rangeQuery;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class OpenSearchPersonQueryUtils {

    private static final String FAMILIE_RELASJON_PATH = "hentPerson.forelderBarnRelasjon";
    private static final String BOSTEDSADRESSE = "hentPerson.bostedsadresse";
    private static final String OPPHOLDSADRESSE = "hentPerson.oppholdsadresse";
    private static final String KONTAKTADRESSE = "hentPerson.kontaktadresse";
    private static final String VEGADRESSE = "vegadresse";
    private static final String MATRIKKELADRESSE = "matrikkeladresse";
    private static final String UTENLANDSKADRESSE = "utenlandskAdresse";
    private static final String KOMMUNENUMMER = "kommunenummer";
    private static final String POSTNUMMER = "postnummer";
    private static final String BYDELSNUMMER = "bydelsnummer";

    public static void addAlderQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        var now = LocalDate.now();
        if (nonNull(request.getPersonRequest().getAlderFom()) || nonNull(request.getPersonRequest().getAlderTom())) {
            queryBuilder.must(QueryBuilders.boolQuery()
                    .must(nestedMatchQuery("hentPerson.foedselsdato", METADATA_HISTORISK, false))
                    .must(QueryBuilders.nestedQuery("hentPerson.foedselsdato",
                            QueryBuilders.boolQuery().must(
                                    rangeQuery("hentPerson.foedselsdato.foedselsdato",
                                            Optional.ofNullable(request.getPersonRequest().getAlderTom())
                                                    .map(now::minusYears)
                                                    .orElse(null),
                                            Optional.ofNullable(request.getPersonRequest().getAlderFom())
                                                    .map(now::minusYears)
                                                    .orElse(null))), ScoreMode.Avg)));
        }
    }

    public static void addHarBarnQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarBarn())) {
            queryBuilder
                    .must(nestedMatchQuery(FAMILIE_RELASJON_PATH, METADATA_HISTORISK, false))
                    .must(nestedMatchQuery(FAMILIE_RELASJON_PATH, "relatertPersonsRolle", "BARN"));
        }
    }

    public static void addHarForeldreQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarForeldre())) {
            queryBuilder
                    .must(nestedMatchQuery(FAMILIE_RELASJON_PATH, METADATA_HISTORISK, false))
                    .must(nestedMatchQuery(FAMILIE_RELASJON_PATH, "minRolleForPerson", "BARN"));
        }
    }

    public static void addSivilstandQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (nonNull(request.getPersonRequest().getSivilstand())) {
            queryBuilder
                    .must(nestedMatchQuery("hentPerson.sivilstand", METADATA_HISTORISK, false))
                    .must(nestedMatchQuery("hentPerson.sivilstand", "type",
                            request.getPersonRequest().getSivilstand().name())
                    );
        }
    }

    public static void addHarDoedfoedtbarnQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarDoedfoedtBarn())) {
            queryBuilder
                    .must(nestedMatchQuery("hentPerson.doedfoedtBarn", METADATA_HISTORISK, false));
        }
    }

    public static void addHarForeldreansvarQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarForeldreAnsvar())) {
            queryBuilder
                    .must(nestedMatchQuery("hentPerson.foreldreansvar", METADATA_HISTORISK, false));
        }
    }

    public static void addVergemaalQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarVerge())) {
            queryBuilder
                    .must(nestedMatchQuery("hentPerson.vergemaalEllerFremtidsfullmakt", METADATA_HISTORISK, false));
        }
    }

    public static void addDoedsfallQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getErDoed())) {
            queryBuilder
                    .must(nestedMatchQuery("hentPerson.doedsfall", METADATA_HISTORISK, false));
        }
    }

    public static void addHarInnflyttingQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarInnflytting())) {
            queryBuilder
                    .must(nestedMatchQuery("hentPerson.innflyttingTilNorge", METADATA_HISTORISK, false));
        }
    }

    public static void addHarUtflyttingQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarUtflytting())) {
            queryBuilder
                    .must(nestedMatchQuery("hentPerson.utflyttingFraNorge", METADATA_HISTORISK, false));
        }
    }

    public static void addAdressebeskyttelseQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getAdresse())
                .filter(adresse -> nonNull(adresse.getAddressebeskyttelse()))
                .ifPresent(adresse ->
                        queryBuilder
                                .must(nestedMatchQuery("hentPerson.adressebeskyttelse", METADATA_HISTORISK, false))
                                .must(nestedMatchQuery("hentPerson.adressebeskyttelse", "gradering",
                                        adresse.getAddressebeskyttelse().name())
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

    private static BoolQueryBuilder addAdresseQuery(String field, String value) {

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
                        queryBuilder
                                .must(nestedMatchQuery(BOSTEDSADRESSE, METADATA_HISTORISK, false))
                                .must(nestedExistQuery(BOSTEDSADRESSE, MATRIKKELADRESSE))
                );
    }

    public static void addHarBostedUkjentQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getAdresse())
                .filter(boadresse -> isTrue(boadresse.getHarUkjentAdresse()))
                .ifPresent(boadresse ->
                        queryBuilder
                                .must(nestedMatchQuery(BOSTEDSADRESSE, METADATA_HISTORISK, false))
                                .must(nestedExistQuery(BOSTEDSADRESSE, "ukjentBosted"))
                );
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
            queryBuilder
                    .must(nestedMatchQuery("hentPerson.statsborgerskap", METADATA_HISTORISK, false))
                    .must(nestedMatchQuery("hentPerson.statsborgerskap", "land",
                            request.getPersonRequest().getStatsborgerskap())
                    );
        }
    }

    public static void addPersonStatusQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getPersonStatus())
                .ifPresent(status -> queryBuilder
                        .must(nestedMatchQuery("hentPerson.folkeregisterpersonstatus", METADATA_HISTORISK, false))
                        .must(nestedMatchQuery("hentPerson.folkeregisterpersonstatus", "status", status)));
    }

    public static void addHarOppholdQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarOpphold())) {
            queryBuilder.must(nestedMatchQuery("hentPerson.opphold", METADATA_HISTORISK, false));
        }
    }

    public static void addHarNyIdentitetQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarNyIdentitet())) {
            queryBuilder
                    .must(nestedMatchQuery(HENT_IDENTER, HISTORISK, false))
                    .must(nestedMatchQuery(HENT_IDENTER, HISTORISK, true)
                    );
        }
    }

    public static void addKjoennQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (nonNull(request.getPersonRequest().getKjoenn())) {
            queryBuilder
                    .must(nestedMatchQuery("hentPerson.kjoenn", METADATA_HISTORISK, false))
                    .must(nestedMatchQuery("hentPerson.kjoenn", "kjoenn",
                            request.getPersonRequest().getKjoenn().name())
                    );
        }
    }

    public static void addKunLevendePersonerQuery(BoolQueryBuilder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getErLevende())) {
            queryBuilder.mustNot(nestedMatchQuery("hentPerson.doedsfall", METADATA_HISTORISK, false));
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
                queryBuilder
                        .must(nestedMatchQuery(FOLKEREGISTERIDENTIFIKATOR, METADATA_HISTORISK, false))
                        .must(nestedMatchQuery(FOLKEREGISTERIDENTIFIKATOR, "type",
                                request.getPersonRequest().getIdenttype().name()));
            }
        }
    }
}
