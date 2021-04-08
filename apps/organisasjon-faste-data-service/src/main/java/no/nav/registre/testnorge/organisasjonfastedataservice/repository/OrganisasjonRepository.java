package no.nav.registre.testnorge.organisasjonfastedataservice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import no.nav.registre.testnorge.organisasjonfastedataservice.domain.Gruppe;
import no.nav.registre.testnorge.organisasjonfastedataservice.repository.model.OrganisasjonModel;

@Repository
public interface OrganisasjonRepository extends CrudRepository<OrganisasjonModel, String> {
    List<OrganisasjonModel> findByGruppe(Gruppe gruppe);
}
