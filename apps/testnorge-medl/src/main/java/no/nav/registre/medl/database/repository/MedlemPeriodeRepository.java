package no.nav.registre.medl.database.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import no.nav.registre.medl.database.model.TMedlemPeriode;

@Repository
public interface MedlemPeriodeRepository extends PagingAndSortingRepository<TMedlemPeriode, Long> {

    @Query(nativeQuery = true, value = "select T_MEDLEM_PERIODE_FUNK_ID_SEQ.nextval from dual")
    long nextFunctionalId();
}
