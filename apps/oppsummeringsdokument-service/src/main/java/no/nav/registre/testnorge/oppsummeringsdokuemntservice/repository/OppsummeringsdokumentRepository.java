package no.nav.registre.testnorge.oppsummeringsdokuemntservice.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import no.nav.registre.testnorge.oppsummeringsdokuemntservice.repository.model.OppsummeringsdokumentModel;


public interface OppsummeringsdokumentRepository extends ElasticsearchRepository<OppsummeringsdokumentModel, String> {
}
