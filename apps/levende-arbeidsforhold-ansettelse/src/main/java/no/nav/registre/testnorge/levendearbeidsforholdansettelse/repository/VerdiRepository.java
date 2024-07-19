package no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository;

import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.VerdierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VerdiRepository extends JpaRepository<VerdierEntity, String> {
    List<VerdierEntity> findAll();

    List<VerdierEntity> findByNavn(String navn);

    //@Query("select verdi_verdi from verdier, jobb_parameter where jobb_parameter.NAVN=verdi_verdi.verdi_navn and verdi_navn=?")

}
