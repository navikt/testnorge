package no.nav.registre.testnorge.avhengighetsanalyseservice.repository;

import org.springframework.data.repository.CrudRepository;

import no.nav.registre.testnorge.avhengighetsanalyseservice.repository.model.DependenciesHistoryModel;

public interface DependenciesHistoryRepository extends CrudRepository<DependenciesHistoryModel, Long> {

}