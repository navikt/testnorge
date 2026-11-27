package no.nav.testnav.dollyfasade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.tenorsearchservice.v1.InfoType;
import no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenorRequestDTO {

    private TenorRequest query;
    private InfoType type;
    private String fields;
    private Integer antall;
    private Integer side;
    private Integer seed;
    private Boolean ikkeFiltrer;
}
