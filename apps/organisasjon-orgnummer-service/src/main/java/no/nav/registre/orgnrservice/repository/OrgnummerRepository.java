package no.nav.registre.orgnrservice.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

import no.nav.registre.orgnrservice.repository.model.OrgnummerModel;

public interface OrgnummerRepository extends CrudRepository<OrgnummerModel, String> {

    Optional<OrgnummerModel> findByOrgnummer(String orgnummer);

    @Query(value = "from OrgnummerModel m where m.ledig = ?1")
    List<OrgnummerModel> findByLedig(boolean ledig);
}
