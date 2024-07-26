package no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository;

import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameter;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.Verdier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VerdiRepository extends JpaRepository<Verdier, Integer> {
    List<Verdier> findAll();

    List<Verdier> findByVerdiNavn(JobbParameter jobbParameter);

    List<Verdier> findVerdierByVerdiNavn(JobbParameter jobbParameter);

    //@Query(value ="select verdier.verdi_verdi from verdier, jobb_parameter where jobb_parameter.NAVN=verdier.verdi_navn and verdier.navn=:navn")
    //List<VerdierEntity> hentVerdier(String navn);
    //@Query("select verdi_verdi from verdier, jobb_parameter where jobb_parameter.NAVN=verdi_verdi.verdi_navn and verdi_navn=?")

}
