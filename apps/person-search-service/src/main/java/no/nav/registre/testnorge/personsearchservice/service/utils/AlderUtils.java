package no.nav.registre.testnorge.personsearchservice.service.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.PersonSearch;
import org.apache.lucene.search.join.ScoreMode;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.RangeQueryBuilder;

import java.time.LocalDate;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@UtilityClass
public class AlderUtils {

    public static void addAlderQueries(BoolQueryBuilder queryBuilder, PersonSearch search) {
        addAlderQuery(queryBuilder, search);
        addFoedselQuery(queryBuilder, search);
    }

    private static void addFoedselQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getFoedsel())
                .ifPresent(value -> {
                    var tom = isNull(value.getTom()) ? LocalDate.now() : value.getTom();
                    queryFoedselsdato(value.getFom(), tom, queryBuilder);
                });
    }

    private static void addAlderQuery(BoolQueryBuilder queryBuilder, PersonSearch search) {
        Optional.ofNullable(search.getAlder())
                .ifPresent(value -> queryAlder(value.getFra(), value.getTil(), queryBuilder));
    }

    private static void queryFoedselsdato(LocalDate fom, LocalDate tom, BoolQueryBuilder queryBuilder) {
        getBetween(fom, tom, "hentPerson.foedsel.foedselsdato")
                .ifPresent(rangeQueryBuilder -> queryBuilder.must(QueryBuilders.nestedQuery(
                                "hentPerson.foedsel",
                                rangeQueryBuilder,
                                ScoreMode.Avg
                        ))
                );
    }

    private static void queryAlder(Short fra, Short til, BoolQueryBuilder queryBuilder) {
        LocalDate now = LocalDate.now();

        if (nonNull(fra) || nonNull(til)) {
            LocalDate tom = nonNull(fra) ? now.minusYears(fra).minusMonths(3) : now;
            LocalDate fom = nonNull(til) ? now.minusYears(til).minusYears(1) : null;

            queryFoedselsdato(fom, tom, queryBuilder);
        }

    }

    private static Optional<RangeQueryBuilder> getBetween(LocalDate fom, LocalDate tom, String field) {
        if (fom == null && tom == null) {
            return Optional.empty();
        }
        var builder = QueryBuilders.rangeQuery(field);

        if (nonNull(fom)) {
            builder.gte(fom);
        }

        if (nonNull(tom)) {
            builder.lte(tom);
        }
        return Optional.of(builder);
    }

}
