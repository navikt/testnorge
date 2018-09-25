package no.nav.identpool.ident.ajourhold.repository;

import java.time.LocalDateTime;
import java.util.List;
import javax.batch.runtime.BatchStatus;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AjourholdRepository extends JpaRepository<AjourholdEntity, Long> {

    default void update(AjourholdEntity entity) {
        entity.setSistOppdatert(LocalDateTime.now());
        this.save(entity);
    }
}
