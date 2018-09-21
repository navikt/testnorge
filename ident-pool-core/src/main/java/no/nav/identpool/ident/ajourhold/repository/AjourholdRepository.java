package no.nav.identpool.ident.ajourhold.repository;

import java.util.List;
import javax.batch.runtime.BatchStatus;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AjourholdRepository extends JpaRepository<AjourholdEntity, Long> {

    List<AjourholdEntity> findByStatus(BatchStatus status);
}
