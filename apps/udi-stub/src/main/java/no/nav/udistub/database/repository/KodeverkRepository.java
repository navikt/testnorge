package no.nav.udistub.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import no.nav.udistub.database.model.Kodeverk;

@Repository
public interface KodeverkRepository extends CrudRepository<Kodeverk, Long> {

    Optional<Kodeverk> findByKode(String kode);

    List<Kodeverk> findAllByType(String type);

    List<Kodeverk> findAllByAktivTomIsNotNull();

    List<Kodeverk> findAllByAktivTomBefore(LocalDate date);

    List<Kodeverk> findAllByAktivFomAfter(LocalDate date);

    List<Kodeverk> findAllByAktivFomAfterAndAktivTomBefore(LocalDate fom, LocalDate tom);

    List<Kodeverk> findAllByAktivTomIsNull();

}
