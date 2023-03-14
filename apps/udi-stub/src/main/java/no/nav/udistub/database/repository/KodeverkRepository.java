package no.nav.udistub.database.repository;

import no.nav.udistub.database.model.Kodeverk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface KodeverkRepository extends JpaRepository<Kodeverk, Long> {

    Optional<Kodeverk> findByKode(String kode);

    List<Kodeverk> findAllByType(String type);

    List<Kodeverk> findAllByAktivTomIsNotNull();

    List<Kodeverk> findAllByAktivTomBefore(LocalDate date);

    List<Kodeverk> findAllByAktivFomAfter(LocalDate date);

    List<Kodeverk> findAllByAktivFomAfterAndAktivTomBefore(LocalDate fom, LocalDate tom);

    List<Kodeverk> findAllByAktivTomIsNull();

}
