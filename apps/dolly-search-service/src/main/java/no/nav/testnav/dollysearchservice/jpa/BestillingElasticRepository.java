package no.nav.testnav.dollysearchservice.jpa;

import no.nav.testnav.libs.data.dollysearchservice.v2.ElasticBestilling;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BestillingElasticRepository extends ElasticsearchRepository<ElasticBestilling, Long> {

}