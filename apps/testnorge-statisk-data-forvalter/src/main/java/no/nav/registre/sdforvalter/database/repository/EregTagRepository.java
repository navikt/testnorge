package no.nav.registre.sdforvalter.database.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

import no.nav.registre.sdforvalter.database.model.EregTagModel;

public interface EregTagRepository extends CrudRepository<EregTagModel, Long> {
    @Query(value = "from EregTagModel m where m.ereg.orgnr = ?1 and m.tag.tag = ?2")
    Optional<EregTagModel> findBy(String orgnr, String tag);

    @Query(value = "from EregTagModel m where m.ereg.orgnr = ?1")
    List<EregTagModel> findAllBy(String orgnr);
}