package no.nav.organisasjonforvalter.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private Integer id;
    private ItemStatus status;

    public enum ItemStatus {NOT_STARTED, RUNNING, COMPLETED, ERROR, FAILED}
}
