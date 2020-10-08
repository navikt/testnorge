package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bruker;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BrukerRepository extends Repository<Bruker, Long> {

    Bruker save(Bruker bruker);

    List<Bruker> findAllByOrderById();

    @Modifying
    @Query(value = "update Bruker set " +
            "eidAv = (select b.id from Bruker b where b.brukerId = :brukerId)" +
            "where navIdent in :navIdenter and eidAv is null")
    int saveNavIdentToBruker(@Param(value = "navIdenter") Collection<String> navIdenter, @Param(value = "brukerId") String brukerId);

    @Modifying
    @Query(value = "update T_BRUKER set EID_AV_ID = null where EID_AV_ID = (select ID from T_BRUKER where BRUKER_ID = :brukerId)", nativeQuery = true)
    int deleteNavIdentToBruker(@Param("brukerId") String brukerId);

    @Modifying
    @Query(value = "update Bruker set migrert = 1 where brukerId = :brukerId")
    int saveBrukerIdMigrert(@Param(value = "brukerId") String brukerId);

    @Modifying
    @Query(value = "update Bruker set migrert = null where brukerId = :brukerId")
    int DeleteBrukerIdMigrert(@Param(value = "brukerId") String brukerId);

    Optional<Bruker> findBrukerByBrukerId(String brukerId);

    Optional<Bruker> findBrukerByNavIdent(String navIdent);

    @Modifying
    @Query(value = "delete from T_BRUKER_FAVORITTER where gruppe_id = :groupId", nativeQuery = true)
    int deleteBrukerFavoritterByGroupId(@Param("groupId") Long groupId);

    @Query(value = "from Bruker b where b.eidAv = :bruker")
    List<Bruker> fetchEidAv(@Param("bruker") Bruker bruker);

}