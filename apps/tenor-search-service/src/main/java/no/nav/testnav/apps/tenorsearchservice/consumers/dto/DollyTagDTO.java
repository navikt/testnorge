package no.nav.testnav.apps.tenorsearchservice.consumers.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DollyTagDTO {

    private String ident;
    private boolean hasDollyTag;
}
