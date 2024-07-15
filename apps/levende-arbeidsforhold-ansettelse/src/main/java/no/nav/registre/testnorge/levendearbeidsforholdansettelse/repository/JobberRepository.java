package no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository;

import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.Jobber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobberRepository extends JpaRepository<Jobber, Long> {

    Jobber save(Jobber jobb);
    Optional<List<Jobber>> findAllById(String id);
}
