package no.nav.testnav.personfastedataservice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import no.nav.testnav.personfastedataservice.domain.Gruppe;
import no.nav.testnav.personfastedataservice.repository.model.PersonEntity;

@Repository
public interface PersonRepository extends CrudRepository<PersonEntity, String> {
    List<PersonEntity> findAllByGruppe(Gruppe gruppe);
}
