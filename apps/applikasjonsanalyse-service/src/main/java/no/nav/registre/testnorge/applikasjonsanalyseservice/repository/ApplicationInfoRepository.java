package no.nav.registre.testnorge.applikasjonsanalyseservice.repository;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

import no.nav.registre.testnorge.applikasjonsanalyseservice.repository.model.ApplicationDependencyModel;
import no.nav.registre.testnorge.applikasjonsanalyseservice.repository.model.ApplicationInfoModel;

public interface ApplicationInfoRepository extends CrudRepository<ApplicationInfoModel, Long> {
    Optional<ApplicationInfoModel> findByNameAndClusterAndNamespace(String name, String cluster, String namespace);
}
