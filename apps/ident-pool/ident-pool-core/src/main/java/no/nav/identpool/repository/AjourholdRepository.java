package no.nav.identpool.repository;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

import no.nav.identpool.domain.Ajourhold;

public interface AjourholdRepository extends JpaRepository<Ajourhold, Long> {

    default void update(Ajourhold entity) {
        entity.setSistOppdatert(LocalDateTime.now());
        this.save(entity);
    }
}
