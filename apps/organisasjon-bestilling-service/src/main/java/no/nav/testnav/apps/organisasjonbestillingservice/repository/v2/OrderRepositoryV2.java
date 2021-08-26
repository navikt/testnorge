package no.nav.testnav.apps.organisasjonbestillingservice.repository.v2;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

import no.nav.testnav.apps.organisasjonbestillingservice.repository.v2.entity.OrderEntity;

public interface OrderRepositoryV2 extends CrudRepository<OrderEntity, Long> {

    Optional<OrderEntity> findById(Long id);

    @Query(value = "from OrderEntity o1 where o1.uuid = ?1")
    List<OrderEntity> findBy(String uuid);
}
