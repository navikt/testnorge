package no.nav.registre.testnorge.arbeidsforhold.repository.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

import no.nav.registre.testnorge.arbeidsforhold.repository.elastic.model.OppsummeringsdokumentModel;

public interface OppsummeringsdokumentRepository extends ElasticsearchRepository<OppsummeringsdokumentModel, String> {
    List<OppsummeringsdokumentModel> findByTitle(String title);
}
