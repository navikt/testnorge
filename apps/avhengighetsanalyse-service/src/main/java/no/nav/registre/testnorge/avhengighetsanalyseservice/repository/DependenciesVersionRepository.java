package no.nav.registre.testnorge.avhengighetsanalyseservice.repository;

import org.springframework.data.repository.CrudRepository;

import no.nav.registre.testnorge.avhengighetsanalyseservice.repository.model.DependenciesVersionModel;

public interface DependenciesVersionRepository extends CrudRepository<DependenciesVersionModel, Long> {

}