package no.nav.dolly.elastic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.elastic.ElasticBestilling;
import no.nav.dolly.elastic.dto.SearchResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQueryBuilder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RandomSearchHelperService {

    private static final int WINDOW_SIZE = 10;
    private static final int FACTOR = 2;

    private final ElasticsearchOperations elasticsearchOperations;
    private Random random = new SecureRandom();

    public SearchResponse search(Criteria criteria) {

        var hits = search(criteria, 0);
        var currentPage = 0;

        if (hits.getTotalHits() > WINDOW_SIZE * FACTOR) {
            var noOfPages = (int) hits.getTotalHits() / (WINDOW_SIZE * FACTOR);
            currentPage = random.nextInt(noOfPages);
            log.info("Total hits={}, noOfPages={}, currentPage={}", hits.getTotalHits(), noOfPages, currentPage);
            hits = search(criteria, currentPage);
        }

        var identer = hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(ElasticBestilling::getIdenter)
                .flatMap(Collection::stream)
                .distinct()
                .collect(toShuffledList());

        return SearchResponse.builder()
                .identer(identer.subList(0, Math.min(identer.size(), WINDOW_SIZE)))
                .totalHits(hits.getTotalHits())
                .pageNumber(currentPage)
                .pageSize(WINDOW_SIZE * FACTOR)
                .windowSize(WINDOW_SIZE)
                .build();
    }

    private static final Collector<?, ?, ?> SHUFFLER = Collectors.collectingAndThen(
            Collectors.toCollection(ArrayList::new),
            list -> {
                Collections.shuffle(list);
                return list;
            }
    );

    @SuppressWarnings("unchecked")
    private static <T> Collector<T, ?, List<T>> toShuffledList() {
        return (Collector<T, ?, List<T>>) SHUFFLER;
    }

    private SearchHits<ElasticBestilling> search(Criteria criteria, int pageNo) {

        return elasticsearchOperations.search(new CriteriaQueryBuilder(criteria)
                        .withPageable(Pageable.ofSize(WINDOW_SIZE * FACTOR).withPage(pageNo))
                        .withTimeout(Duration.ofSeconds(3))
                        .build(),
                ElasticBestilling.class, IndexCoordinates.of("bestilling"));
    }
}