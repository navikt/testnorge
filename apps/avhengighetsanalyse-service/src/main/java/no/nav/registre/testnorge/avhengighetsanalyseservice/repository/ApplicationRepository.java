package no.nav.registre.testnorge.avhengighetsanalyseservice.repository;

import org.springframework.data.repository.CrudRepository;

import no.nav.registre.testnorge.avhengighetsanalyseservice.repository.model.ApplicationModel;

public interface ApplicationRepository extends CrudRepository<ApplicationModel, String> {

}