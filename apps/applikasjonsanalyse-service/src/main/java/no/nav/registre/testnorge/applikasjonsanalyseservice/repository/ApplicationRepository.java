package no.nav.registre.testnorge.applikasjonsanalyseservice.repository;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

import no.nav.registre.testnorge.applikasjonsanalyseservice.repository.model.ApplicationModel;

public interface ApplicationRepository extends CrudRepository<ApplicationModel, String> {

    Optional<ApplicationModel> findByShaAndRepositoryAndOrganisation(String sha, String repository, String organisation);
}
