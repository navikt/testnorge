package no.nav.testnav.apps.organisasjonbestillingservice.domain.v2;


import lombok.Value;

import no.nav.testnav.libs.dto.organiasjonbestilling.v2.OrderDTO;
import no.nav.testnav.apps.organisasjonbestillingservice.repository.v2.entity.OrderEntity;

@Value
public class Order {
    Long id;
    Long queueId;
    Long buildId;
    Long batchId;
    String miljo;
    String uuid;

    public Order(OrderDTO dto) {
        id = dto.getId();
        queueId = dto.getQueueId();
        buildId = dto.getBuildId();
        batchId = dto.getBatchId();
        miljo = dto.getMiljo();
        uuid = dto.getUuid();
    }

    public Order(OrderEntity entity) {
        id = entity.getId();
        queueId = entity.getQueueId();
        buildId = entity.getBuildId();
        uuid = entity.getUuid();
        batchId = entity.getBatchId();
        miljo = entity.getMiljo();
    }

    public OrderEntity toEntity() {
        return OrderEntity
                .builder()
                .id(id)
                .buildId(buildId)
                .queueId(queueId)
                .miljo(miljo)
                .batchId(batchId)
                .uuid(uuid)
                .build();
    }

    public OrderDTO toDTO() {
        return OrderDTO
                .builder()
                .id(id)
                .buildId(buildId)
                .queueId(queueId)
                .miljo(miljo)
                .batchId(batchId)
                .uuid(uuid)
                .build();
    }

}
