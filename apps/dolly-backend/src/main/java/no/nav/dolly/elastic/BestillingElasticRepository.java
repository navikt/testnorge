package no.nav.dolly.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Collection;
import java.util.List;

public interface BestillingElasticRepository extends ElasticsearchRepository<ElasticBestilling, Long> {

    List<ElasticBestilling> getAllByIdenterIn(Collection<String> identer);
}
