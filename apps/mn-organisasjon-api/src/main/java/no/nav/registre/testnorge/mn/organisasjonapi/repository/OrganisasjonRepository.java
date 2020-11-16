package no.nav.registre.testnorge.mn.organisasjonapi.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import no.nav.registre.testnorge.mn.organisasjonapi.repository.model.OrganisasjonModel;

public interface OrganisasjonRepository extends CrudRepository<OrganisasjonModel, String> {
    Optional<OrganisasjonModel> findByOrgnummerAndEnvironment(String orgnummer, String environment);

    @Transactional
    void deleteByOrgnummerAndEnvironment(String orgnummer, String environment);

    List<OrganisasjonModel> findAllByActiveAndEnvironment(Boolean active, String environment);

    List<OrganisasjonModel> findAllByEnvironment(String environment);
}