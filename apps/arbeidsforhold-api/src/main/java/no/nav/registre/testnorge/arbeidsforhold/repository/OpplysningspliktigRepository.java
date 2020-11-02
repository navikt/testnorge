package no.nav.registre.testnorge.arbeidsforhold.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

import no.nav.registre.testnorge.arbeidsforhold.repository.model.OpplysningspliktigModel;

public interface OpplysningspliktigRepository extends CrudRepository<OpplysningspliktigModel, String> {

    @Query(value = "from OpplysningspliktigModel o1 where o1.rapporteringsmaaned = ?1 AND o1.orgnummer = ?2 AND o1.miljo = ?3 AND o1.version = (select max(version) from OpplysningspliktigModel o2 where o2.rapporteringsmaaned = ?1 AND o2.orgnummer = ?2 AND o2.miljo = ?3)")
    Optional<OpplysningspliktigModel> findLast(String rapporteringsmaaned, String orgnummer, String miljo);

}