package no.nav.testnav.dollysearchservice.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import no.nav.testnav.libs.data.pdlforvalter.v1.Identtype;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders;

import java.time.LocalDate;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.CONCAT;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.FOLKEREGISTERIDENTIFIKATOR;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.HENT_IDENTER;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.HISTORISK;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.METADATA_HISTORISK;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.nestedExistQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.nestedMatchQuery;
import static no.nav.testnav.dollysearchservice.utils.OpenSearchQueryUtils.nestedRangeQuery;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class OpenSearchPersonQueryUtils {

    private static final String FAMILIE_RELASJON_PATH = "hentPerson.forelderBarnRelasjon";
    private static final String FOEDSLESDATO = "hentPerson.foedselsdato";
    private static final String BOSTEDSADRESSE = "hentPerson.bostedsadresse";
    private static final String OPPHOLDSADRESSE = "hentPerson.oppholdsadresse";
    private static final String KONTAKTADRESSE = "hentPerson.kontaktadresse";
    private static final String VEGADRESSE = "vegadresse";
    private static final String MATRIKKELADRESSE = "matrikkeladresse";
    private static final String UTENLANDSKADRESSE = "utenlandskAdresse";
    private static final String KOMMUNENUMMER = "kommunenummer";
    private static final String POSTNUMMER = "postnummer";
    private static final String BYDELSNUMMER = "bydelsnummer";

    public static void addAlderQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        var now = LocalDate.now();
        if (nonNull(request.getPersonRequest().getAlderFom()) && nonNull(request.getPersonRequest().getAlderTom())) {

            queryBuilder.must(q -> q.nested(nestedMatchQuery(FOEDSLESDATO, METADATA_HISTORISK, false)))
                    .must(q -> q.nested(nestedRangeQuery(FOEDSLESDATO,
                            "foedselsdato",
                            now.minusYears(request.getPersonRequest().getAlderTom()).minusYears(1),
                            now.minusYears(request.getPersonRequest().getAlderFom()).minusDays(1))));
        }
    }

    public static void addFoedselsdatoQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        if (nonNull(request.getPersonRequest().getFoedselsdatoFom()) && nonNull(request.getPersonRequest().getFoedselsdatoTom())) {

            queryBuilder.must(q -> q.nested(nestedMatchQuery(FOEDSLESDATO, METADATA_HISTORISK, false)))
                    .must(q -> q.nested(nestedRangeQuery(FOEDSLESDATO,
                            "foedselsdato",
                            request.getPersonRequest().getFoedselsdatoFom(),
                            request.getPersonRequest().getFoedselsdatoTom())));
        }
    }

    public static void addHarBarnQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarBarn())) {
            queryBuilder
                    .must(q -> q.nested(nestedMatchQuery(FAMILIE_RELASJON_PATH, METADATA_HISTORISK, false)))
                    .must(q -> q.nested(nestedMatchQuery(FAMILIE_RELASJON_PATH, "relatertPersonsRolle", "BARN")));
        }
    }

    public static void addHarForeldreQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarForeldre())) {
            queryBuilder
                    .must(q -> q.nested(nestedMatchQuery(FAMILIE_RELASJON_PATH, METADATA_HISTORISK, false)))
                    .must(q -> q.nested(nestedMatchQuery(FAMILIE_RELASJON_PATH, "minRolleForPerson", "BARN")));
        }
    }

    public static void addSivilstandQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        if (nonNull(request.getPersonRequest().getSivilstand())) {
            queryBuilder
                    .must(q -> q.nested(nestedMatchQuery("hentPerson.sivilstand", METADATA_HISTORISK, false)))
                    .must(q -> q.nested(nestedMatchQuery("hentPerson.sivilstand", "type",
                            request.getPersonRequest().getSivilstand().name())));
        }
    }

    public static void addHarDoedfoedtbarnQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarDoedfoedtBarn())) {
            queryBuilder
                    .must(q -> q.nested(nestedMatchQuery("hentPerson.doedfoedtBarn", METADATA_HISTORISK, false)));
        }
    }

    public static void addHarForeldreansvarQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarForeldreAnsvar())) {
            queryBuilder
                    .must(q -> q.nested(nestedMatchQuery("hentPerson.foreldreansvar", METADATA_HISTORISK, false)));
        }
    }

    public static void addVergemaalQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarVerge())) {
            queryBuilder
                    .must(q -> q.nested(nestedMatchQuery("hentPerson.vergemaalEllerFremtidsfullmakt", METADATA_HISTORISK, false)));
        }
    }

    public static void addDoedsfallQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getErDoed())) {
            queryBuilder
                    .must(q -> q.nested(nestedMatchQuery("hentPerson.doedsfall", METADATA_HISTORISK, false)));
        }
    }

    public static void addHarInnflyttingQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarInnflytting())) {
            queryBuilder
                    .must(q -> q.nested(nestedMatchQuery("hentPerson.innflyttingTilNorge", METADATA_HISTORISK, false)));
        }
    }

    public static void addHarUtflyttingQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarUtflytting())) {
            queryBuilder
                    .must(q -> q.nested(nestedMatchQuery("hentPerson.utflyttingFraNorge", METADATA_HISTORISK, false)));
        }
    }

    public static void addAdressebeskyttelseQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getAdresse())
                .filter(adresse -> nonNull(adresse.getAddressebeskyttelse()))
                .ifPresent(adresse ->
                        queryBuilder
                                .must(q -> q.nested(nestedMatchQuery("hentPerson.adressebeskyttelse", METADATA_HISTORISK, false)))
                                .must(q -> q.nested(nestedMatchQuery("hentPerson.adressebeskyttelse", "gradering",
                                        adresse.getAddressebeskyttelse().name()))));
    }

    public static void addHarBostedsadresseQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getAdresse())
                .filter(adresse -> isTrue(adresse.getHarBostedsadresse()))
                .ifPresent(adresse ->
                        queryBuilder.must(q -> q.nested(nestedMatchQuery(BOSTEDSADRESSE, METADATA_HISTORISK, false))));
    }

    public static void addHarOppholdsadresseQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getAdresse())
                .filter(adresse -> isTrue(adresse.getHarOppholdsadresse()))
                .ifPresent(adresse ->
                        queryBuilder.must(q -> q.nested(nestedMatchQuery(OPPHOLDSADRESSE, METADATA_HISTORISK, false))));
    }

    public static void addHarKontaktadresseQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getAdresse())
                .filter(adresse -> isTrue(adresse.getHarKontaktadresse()))
                .ifPresent(adresse ->
                        queryBuilder.must(q -> q.nested(nestedMatchQuery(KONTAKTADRESSE, METADATA_HISTORISK, false))));
    }

    private static BoolQuery addAdresseQuery(String field, String value) {

        return QueryBuilders.bool()
                .should(q1 -> q1.bool(QueryBuilders.bool()
                        .must(q2 -> q2.nested(nestedMatchQuery(BOSTEDSADRESSE, METADATA_HISTORISK, false)))
                        .must(q2 -> q2.bool(QueryBuilders.bool()
                                .should(q3 -> q3.nested(nestedMatchQuery(BOSTEDSADRESSE, CONCAT.formatted(VEGADRESSE, field), value)))
                                .should(q3 -> q3.nested(nestedMatchQuery(BOSTEDSADRESSE, CONCAT.formatted(MATRIKKELADRESSE, field), value)))
                                .build()))
                        .build()
                ))
                .should(q1 -> q1.bool(QueryBuilders.bool()
                        .must(q2 -> q2.nested(nestedMatchQuery(OPPHOLDSADRESSE, METADATA_HISTORISK, false)))
                        .must(q2 -> q2.bool(QueryBuilders.bool()
                                .should(q3 -> q3.nested(nestedMatchQuery(OPPHOLDSADRESSE, CONCAT.formatted(VEGADRESSE, field), value)))
                                .should(q3 -> q3.nested(nestedMatchQuery(OPPHOLDSADRESSE, CONCAT.formatted(MATRIKKELADRESSE, field), value)))
                                .build()))
                        .build()
                ))
                .should(q1 -> q1.bool(QueryBuilders.bool()
                        .must(q2 -> q2.nested(nestedMatchQuery(KONTAKTADRESSE, METADATA_HISTORISK, false)))
                        .must(q2 -> q2.nested(nestedMatchQuery(KONTAKTADRESSE, CONCAT.formatted(VEGADRESSE, field), value)))
                        .build()
                ))
                .build();
    }

    public static void addAdresseKommunenrQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getAdresse())
                .filter(adresse -> isNotBlank(adresse.getKommunenummer()))
                .ifPresent(adresse ->
                        queryBuilder.must(q1 -> q1.bool(QueryBuilders.bool()
                                .should(q2 -> q2.bool(addAdresseQuery(KOMMUNENUMMER, adresse.getKommunenummer())))
                                .should(q2 -> q2.bool(QueryBuilders.bool()
                                        .must(q3 -> q3.nested(nestedMatchQuery(BOSTEDSADRESSE, METADATA_HISTORISK, false)))
                                        .must(q3 -> q3.nested(nestedMatchQuery(BOSTEDSADRESSE, "ukjentBosted.bostedskommune", adresse.getKommunenummer())))
                                        .build()))
                                .build()
                        )));
    }

    public static void addAdressePostnrQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getAdresse())
                .filter(adresse -> isNotBlank(adresse.getPostnummer()))
                .ifPresent(adresse ->
                        queryBuilder.must(q -> q.bool(QueryBuilders.bool()
                                .should(q2 -> q2.bool(addAdresseQuery(POSTNUMMER, adresse.getPostnummer())))
                                .should(q2 -> q2.bool(QueryBuilders.bool()
                                        .must(q3 -> q3.nested(nestedMatchQuery(KONTAKTADRESSE, METADATA_HISTORISK, false)))
                                        .must(q3 -> q3.nested(nestedMatchQuery(KONTAKTADRESSE, "postboksadresse." + POSTNUMMER, adresse.getPostnummer())))
                                        .build()))
                                .build()
                        )));
    }

    public static void addAdresseBydelsnrQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getAdresse())
                .filter(adresse -> isNotBlank(adresse.getBydelsnummer()))
                .ifPresent(adresse ->
                        queryBuilder.must(q1 -> q1.bool(addAdresseQuery(BYDELSNUMMER, adresse.getBydelsnummer()))));
    }

    public static void addHarAdresseBydelsnummerQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getAdresse())
                .filter(adresse -> isTrue(adresse.getHarBydelsnummer()))
                .ifPresent(adresse ->
                        queryBuilder.must(q -> q.bool(QueryBuilders.bool()
                                .should(q1 -> q1.bool(QueryBuilders.bool()
                                        .must(q2 -> q2.nested(nestedMatchQuery(BOSTEDSADRESSE, METADATA_HISTORISK, false)))
                                        .must(q2 -> q2.bool(QueryBuilders.bool()
                                                .should(q3 -> q3.nested(nestedExistQuery(BOSTEDSADRESSE, CONCAT.formatted(VEGADRESSE, BYDELSNUMMER))))
                                                .should(q3 -> q3.nested(nestedExistQuery(BOSTEDSADRESSE, CONCAT.formatted(MATRIKKELADRESSE, BYDELSNUMMER))))
                                                .build()))
                                        .build()))
                                .should(q1 -> q1.bool(QueryBuilders.bool()
                                        .must(q2 -> q2.nested(nestedMatchQuery(OPPHOLDSADRESSE, METADATA_HISTORISK, false)))
                                        .must(q2 -> q2.bool(QueryBuilders.bool()
                                                .should(q3 -> q3.nested(nestedExistQuery(OPPHOLDSADRESSE, CONCAT.formatted(VEGADRESSE, BYDELSNUMMER))))
                                                .should(q3 -> q3.nested(nestedExistQuery(OPPHOLDSADRESSE, CONCAT.formatted(MATRIKKELADRESSE, BYDELSNUMMER))))
                                                .build()))
                                        .build()))
                                .should(q1 -> q1.bool(QueryBuilders.bool()
                                        .must(q2 -> q2.nested(nestedMatchQuery(KONTAKTADRESSE, METADATA_HISTORISK, false)))
                                        .must(q2 -> q2.nested(nestedExistQuery(KONTAKTADRESSE, CONCAT.formatted(VEGADRESSE, BYDELSNUMMER))))
                                        .build()))
                                .build()
                        )));
    }

    public static void addAdresseUtlandQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getAdresse())
                .filter(boadresse -> isTrue(boadresse.getHarUtenlandsadresse()))
                .ifPresent(boadresse ->
                        queryBuilder.must(q1 -> q1.bool(QueryBuilders.bool()
                                .should(q2 -> q2.bool(QueryBuilders.bool()
                                        .must(q3 -> q3.nested(nestedExistQuery(BOSTEDSADRESSE, UTENLANDSKADRESSE)))
                                        .must(q3 -> q3.nested(nestedMatchQuery(BOSTEDSADRESSE, METADATA_HISTORISK, false)))
                                        .build()))
                                .should(q2 -> q2.bool(QueryBuilders.bool()
                                        .must(q3 -> q3.nested(nestedExistQuery(OPPHOLDSADRESSE, UTENLANDSKADRESSE)))
                                        .must(q3 -> q3.nested(nestedMatchQuery(OPPHOLDSADRESSE, METADATA_HISTORISK, false)))
                                        .build()))
                                .should(q2 -> q2.bool(QueryBuilders.bool()
                                        .must(q3 -> q3.nested(nestedExistQuery(KONTAKTADRESSE, UTENLANDSKADRESSE)))
                                        .must(q3 -> q3.nested(nestedMatchQuery(KONTAKTADRESSE, METADATA_HISTORISK, false)))
                                        .build()
                                ))
                                .build())));
    }

    public static void addAdresseMatrikkelQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getAdresse())
                .filter(boadresse -> isTrue(boadresse.getHarMatrikkeladresse()))
                .ifPresent(boadresse ->
                        queryBuilder
                                .must(q -> q.nested(nestedMatchQuery(BOSTEDSADRESSE, METADATA_HISTORISK, false)))
                                .must(q -> q.nested(nestedExistQuery(BOSTEDSADRESSE, MATRIKKELADRESSE)))
                );
    }

    public static void addHarBostedUkjentQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getAdresse())
                .filter(boadresse -> isTrue(boadresse.getHarUkjentAdresse()))
                .ifPresent(boadresse ->
                        queryBuilder
                                .must(q -> q.nested(nestedMatchQuery(BOSTEDSADRESSE, METADATA_HISTORISK, false)))
                                .must(q -> q.nested(nestedExistQuery(BOSTEDSADRESSE, "ukjentBosted")))
                );
    }

    public static void addHarDeltBostedQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getAdresse())
                .filter(adresse -> isTrue(adresse.getHarDeltBosted()))
                .ifPresent(adresse ->
                        queryBuilder.must(q -> q.nested(nestedMatchQuery("hentPerson.deltBosted", METADATA_HISTORISK, false)))
                );
    }

    public static void addHarKontaktinformasjonForDoedsboQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarKontaktinformasjonForDoedsbo())) {
            queryBuilder.must(q -> q.nested(nestedMatchQuery("hentPerson.kontaktinformasjonForDoedsbo", METADATA_HISTORISK, false)));
        }
    }

    public static void addHarUtenlandskIdentifikasjonsnummerQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarUtenlandskIdentifikasjonsnummer())) {
            queryBuilder.must(q -> q.nested(nestedMatchQuery("hentPerson.utenlandskIdentifikasjonsnummer", METADATA_HISTORISK, false)));
        }
    }

    public static void addHarFalskIdentitetQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarFalskIdentitet())) {
            queryBuilder.must(q -> q.nested(nestedMatchQuery("hentPerson.falskIdentitet", METADATA_HISTORISK, false)));
        }
    }

    public static void addHarTilrettelagtKommunikasjonQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarTilrettelagtKommunikasjon())) {
            queryBuilder.must(q -> q.nested(nestedMatchQuery("hentPerson.tilrettelagtKommunikasjon", METADATA_HISTORISK, false)));
        }
    }

    public static void addHarSikkerhetstiltakQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarSikkerhetstiltak())) {
            queryBuilder.must(q -> q.nested(nestedMatchQuery("hentPerson.sikkerhetstiltak", METADATA_HISTORISK, false)));
        }
    }

    public static void addStatsborgerskapQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        if (isNotBlank(request.getPersonRequest().getStatsborgerskap())) {
            queryBuilder
                    .must(q -> q.nested(nestedMatchQuery("hentPerson.statsborgerskap", METADATA_HISTORISK, false)))
                    .must(q -> q.nested(nestedMatchQuery("hentPerson.statsborgerskap", "land",
                            request.getPersonRequest().getStatsborgerskap()))
                    );
        }
    }

    public static void addPersonStatusQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        Optional.ofNullable(request.getPersonRequest().getPersonStatus())
                .ifPresent(status -> queryBuilder
                        .must(q -> q.nested(nestedMatchQuery("hentPerson.folkeregisterpersonstatus", METADATA_HISTORISK, false)))
                        .must(q -> q.nested(nestedMatchQuery("hentPerson.folkeregisterpersonstatus", "status", status))));
    }

    public static void addHarOppholdQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarOpphold())) {
            queryBuilder.must(q -> q.nested(nestedMatchQuery("hentPerson.opphold", METADATA_HISTORISK, false)));
        }
    }

    public static void addHarNyIdentitetQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getHarNyIdentitet())) {
            queryBuilder
                    .must(q -> q.nested(nestedMatchQuery(HENT_IDENTER, HISTORISK, false)))
                    .must(q -> q.nested(nestedMatchQuery(HENT_IDENTER, HISTORISK, true)));
        }
    }

    public static void addKjoennQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        if (nonNull(request.getPersonRequest().getKjoenn())) {
            queryBuilder
                    .must(q -> q.nested(nestedMatchQuery("hentPerson.kjoenn", METADATA_HISTORISK, false)))
                    .must(q -> q.nested(nestedMatchQuery("hentPerson.kjoenn", "kjoenn",
                            request.getPersonRequest().getKjoenn().name())));
        }
    }

    public static void addKunLevendePersonerQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        if (isTrue(request.getPersonRequest().getErLevende())) {
            queryBuilder.mustNot(q -> q.nested(nestedMatchQuery("hentPerson.doedsfall", METADATA_HISTORISK, false)));
        }
    }

    public static void addIdenttypeQuery(BoolQuery.Builder queryBuilder, SearchRequest request) {

        if (nonNull(request.getPersonRequest().getIdenttype())) {
            if (request.getPersonRequest().getIdenttype() == Identtype.NPID) {
                queryBuilder
                        .must(q -> q.nested(nestedMatchQuery(HENT_IDENTER, HISTORISK, false)))
                        .must(q -> q.nested(nestedMatchQuery(HENT_IDENTER, "gruppe", "NPID")));
            } else {
                queryBuilder
                        .must(q -> q.nested(nestedMatchQuery(FOLKEREGISTERIDENTIFIKATOR, METADATA_HISTORISK, false)))
                        .must(q -> q.nested(nestedMatchQuery(FOLKEREGISTERIDENTIFIKATOR, "type",
                                request.getPersonRequest().getIdenttype().name())));
            }
        }
    }
}
