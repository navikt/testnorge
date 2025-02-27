package no.nav.testnav.libs.data.dollysearchservice.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.data.dollysearchservice.v1.PersonRequest;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

    @Schema(description = "Sidenummer")
    private Integer side;
    @Schema(description = "Antall resultater per side")
    private Integer antall;
    @Schema(description = "Seed for paginering")
    private Integer seed;

    @Schema(description = "Persondetaljer")
    private PersonRequest personRequest;
}
