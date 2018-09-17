package no.nav.identpool.ident.batch.repository;

import java.util.List;
import javax.batch.runtime.BatchStatus;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchRepository extends JpaRepository<BatchEntity, Long> {

    default List<BatchEntity> findAllByWhereStatusIsStarted() {
        return findByStatus(BatchStatus.STARTED);
    }

    default BatchEntity findFirstWhereStatusIsStartedOrderBySluttDatoDesc() {
        return findFirstByStatusOrderBySluttDatoDesc(BatchStatus.STARTED);
    }

    List<BatchEntity> findByStatus(BatchStatus status);

    BatchEntity findFirstByStatusOrderBySluttDatoDesc(BatchStatus status);
}
