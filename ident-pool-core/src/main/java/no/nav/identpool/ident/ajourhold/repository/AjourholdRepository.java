package no.nav.identpool.ident.ajourhold.repository;

import java.util.List;
import javax.batch.runtime.BatchStatus;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AjourholdRepository extends JpaRepository<AjourholdEntity, Long> {

    default List<AjourholdEntity> findAllByWhereStatusIsStarted() {
        return findByStatus(BatchStatus.STARTED);
    }

    default AjourholdEntity findFirstWhereStatusIsStartedOrderBySluttDatoDesc() {
        return findFirstByStatusOrderBySluttDatoDesc(BatchStatus.STARTED);
    }

    List<AjourholdEntity> findByStatus(BatchStatus status);

    AjourholdEntity findFirstByStatusOrderBySluttDatoDesc(BatchStatus status);
}
