package no.nav.identpool.repository.oracle;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

import no.nav.identpool.domain.oracle.OraAjourhold;

public interface OraAjourholdRepository extends JpaRepository<OraAjourhold, Long> {

    default void update(OraAjourhold entity) {
        entity.setSistOppdatert(LocalDateTime.now());
        this.save(entity);
    }
}
