package no.nav.registre.testnorge.oppsummeringsdokumentservice.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import no.nav.registre.testnorge.oppsummeringsdokumentservice.repository.model.OppsummeringsdokumentModel;


public interface OppsummeringsdokumentRepository extends ElasticsearchRepository<OppsummeringsdokumentModel, String> {
}
