package no.nav.registre.testnorge.applikasjonsanalyseservice.repository;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

import no.nav.registre.testnorge.applikasjonsanalyseservice.repository.model.ApplicationDependencyModel;
import no.nav.registre.testnorge.applikasjonsanalyseservice.repository.model.ApplicationInfoDependencyModel;

public interface ApplicationDependencyRepository extends CrudRepository<ApplicationDependencyModel, Long> {
    Optional<ApplicationDependencyModel> findByNameAndClusterAndNamespace(String name, String cluster, String namespace);
}
