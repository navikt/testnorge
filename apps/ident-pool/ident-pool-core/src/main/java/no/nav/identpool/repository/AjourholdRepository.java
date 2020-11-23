package no.nav.identpool.repository;

import java.time.LocalDateTime;

import no.nav.identpool.domain.Ajourhold;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AjourholdRepository extends JpaRepository<Ajourhold, Long> {

    default void update(Ajourhold entity) {
        entity.setSistOppdatert(LocalDateTime.now());
        this.save(entity);
    }
}
