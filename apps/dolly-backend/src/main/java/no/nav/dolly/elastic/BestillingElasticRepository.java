package no.nav.dolly.elastic;

import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;

public interface BestillingElasticRepository extends ReactiveElasticsearchRepository<ElasticBestilling, Long> {

}
