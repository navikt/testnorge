package no.nav.registre.testnorge.organisasjonbestillingservice.domain;

import no.nav.registre.testnorge.libs.dto.organiasjonbestilling.v1.OrderDTO;
import no.nav.registre.testnorge.organisasjonbestillingservice.repository.model.OrderModel;

public class Order {
    private final Long batchId;
    private final String miljo;
    private final String uuid;

    public Order(OrderDTO dto, String uuid) {
        this.batchId = dto.getBatchId();
        this.miljo = dto.getMiljo();
        this.uuid = uuid;
    }

    public Order(OrderModel model) {
        this.miljo = model.getMiljo();
        this.uuid = model.getUuid();
        this.batchId = model.getBatchId();
    }

    public String getUuid() {
        return uuid;
    }

    public String getMiljo() {
        return miljo;
    }

    public Long getBatchId() {
        return batchId;
    }

    public OrderModel toModel(Long id) {
        return OrderModel
                .builder()
                .id(id)
                .batchId(batchId)
                .miljo(miljo)
                .uuid(uuid)
                .build();
    }

}
