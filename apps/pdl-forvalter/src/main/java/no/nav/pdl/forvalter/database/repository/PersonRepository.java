package no.nav.pdl.forvalter.database.repository;

import no.nav.pdl.forvalter.database.model.DbPerson;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PersonRepository extends PagingAndSortingRepository<DbPerson, Long> {

    Optional<DbPerson> findByIdent(String ident);

    List<DbPerson> findByIdentIn(Collection<String> identer, Pageable pageable);

    List<DbPerson> findByIdIn(List<Long> identer);

    @Modifying
    int deleteByIdentIn(Set<String> ident);

    @Modifying
    int deleteByIdent(String ident);

    boolean existsByIdent(String ident);

    @Query("from DbPerson p "
            + "where (:partialIdent is null or :partialIdent is not null and p.ident like %:partialIdent%)"
            + "and (:partialNavn1 is null or :partialNavn1 is not null and (upper(p.etternavn) like %:partialNavn1% or upper(p.fornavn) like %:partialNavn1%))"
            + "and (:partialNavn2 is null or :partialNavn2 is not null and (upper(p.etternavn) like %:partialNavn2% or upper(p.fornavn) like %:partialNavn2%))")
    List<DbPerson> findByWildcardIdent(@Param("partialIdent") String partialIdent,
                                     @Param("partialNavn1") String partialNavn1,
                                     @Param("partialNavn2") String partialNavn2,
                                     Pageable pageable);
}
