package no.nav.dolly.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BestillingElasticRepository extends ElasticsearchRepository<ElasticBestilling, Long> {

}
