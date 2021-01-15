package no.nav.registre.orgnrservice.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import no.nav.registre.orgnrservice.repository.model.OrgnummerModel;


@Repository
public interface OrgnummerRepository extends CrudRepository<OrgnummerModel, Long> {

    List<OrgnummerModel> findAllByLedigIsTrue();

    OrgnummerModel findByOrgnummer(String orgnummer);

    @Modifying
    OrgnummerModel save(OrgnummerModel orgnummerModel);

    @Modifying
    @Query(value = "delete from OrgnummerModel m where m.orgnummer = ?1")
    void deleteByOrgnummer(String orgnummer);
}
