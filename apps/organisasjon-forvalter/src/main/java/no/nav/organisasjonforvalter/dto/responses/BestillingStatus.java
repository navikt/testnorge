package no.nav.organisasjonforvalter.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BestillingStatus {

    private String orgnummer;
    private String miljoe;
    private String uuid;
    private List<ItemDto> itemDtos;
    private StatusDTO status;
    private String feilmelding;

    public List<ItemDto> getItemDtos() {
        if (isNull(itemDtos)) {
            itemDtos = new ArrayList<>();
        }
        return itemDtos;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDto {

        private Integer id;
        private ItemStatus status;

        public enum ItemStatus {INITIALIZING, NOT_STARTED, RUNNING, COMPLETED, ERROR, FAILED}
    }
}
