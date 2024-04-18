package no.nav.testnav.apps.oppsummeringsdokumentservice.repository;

import no.nav.testnav.apps.oppsummeringsdokumentservice.repository.model.OppsummeringsdokumentModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface OppsummeringsdokumentRepository extends ElasticsearchRepository<OppsummeringsdokumentModel, String> {

}
