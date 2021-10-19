package no.nav.identpool.repository;

import no.nav.identpool.domain.Ajourhold;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AjourholdRepository extends JpaRepository<Ajourhold, Long> {

}
