package no.nav.pdl.forvalter.database.repository;

import no.nav.pdl.forvalter.database.model.DbRelasjon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelasjonRepository extends JpaRepository<DbRelasjon, Long> {

    List<DbRelasjon> findByPersonIdent(String ident);

    @Modifying
    @Query("delete from DbRelasjon r " +
            "where r.person.id in (select p.id from DbPerson p where p.ident in (:identer))")
    void deleteByPersonIdentIn(List<String> identer);
}
