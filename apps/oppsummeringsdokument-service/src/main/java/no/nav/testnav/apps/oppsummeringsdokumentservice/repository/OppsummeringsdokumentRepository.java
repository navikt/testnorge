package no.nav.testnav.apps.oppsummeringsdokumentservice.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.Populasjon;
import no.nav.testnav.apps.oppsummeringsdokumentservice.repository.model.OppsummeringsdokumentModel;


public interface OppsummeringsdokumentRepository extends ElasticsearchRepository<OppsummeringsdokumentModel, String> {
    void deleteAllByMiljoAndPopulasjon(String miljo, Populasjon populasjon);

    void deleteAllByMiljo(String miljo);


}
