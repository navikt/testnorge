package no.nav.pdl.forvalter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Paginering {

    @Schema(defaultValue = "0")
    private Integer sidenummer;
    @Schema(defaultValue = "10")
    private Integer sidestoerrelse;
}
