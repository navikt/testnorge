package no.nav.identpool.repository;

import java.time.LocalDateTime;

import no.nav.identpool.domain.AjourholdEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AjourholdRepository extends JpaRepository<AjourholdEntity, Long> {

    default void update(AjourholdEntity entity) {
        entity.setSistOppdatert(LocalDateTime.now());
        this.save(entity);
    }
}
