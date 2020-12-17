package no.nav.registre.orgnrservice.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import no.nav.registre.orgnrservice.repository.model.OrganisasjonModel;


@Repository
public interface OrganisasjonRepository extends CrudRepository<OrganisasjonModel, Long> {

    List<OrganisasjonModel> findAllByLedigIsTrue();
    OrganisasjonModel findByOrgnummer(String orgnummer);

    @Transactional
    @Modifying
    @Query( value = "update OrganisasjonModel m set m.ledig=?2 where m.orgnummer=?1")
    int updateIsLedigByOrgnummer(String orgnummer, boolean ledig);

    @Transactional
    @Modifying
    @Query(value = "delete from OrganisasjonModel m where m.orgnummer = ?1")
    void deleteByOrgnummer( String orgnummer);
}
