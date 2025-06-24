package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdSjekkDTO {

    private String ident;
    private boolean inUse;
    private boolean available;
}
