package no.nav.registre.sdforvalter.database.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

import no.nav.registre.sdforvalter.database.model.TpsIdentTagModel;

public interface TpsIdentTagRepository extends CrudRepository<TpsIdentTagModel, Long> {
    @Query(value = "from TpsIdentTagModel m where m.ident.fnr = ?1 and m.tag.tag = ?2")
    Optional<TpsIdentTagModel> findBy(String ident, String tag);

    @Query(value = "from TpsIdentTagModel m where m.ident.fnr = ?1")
    List<TpsIdentTagModel> findAllByIdent(String ident);
}