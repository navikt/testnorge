package no.nav.registre.testnorge.mn.organisasjonapi.repository;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

import no.nav.registre.testnorge.mn.organisasjonapi.repository.model.OrganisasjonModel;

public interface OrgansiasjonRepository extends CrudRepository<OrganisasjonModel, String> {
    List<OrganisasjonModel> findAllByActive(Boolean active);
}
