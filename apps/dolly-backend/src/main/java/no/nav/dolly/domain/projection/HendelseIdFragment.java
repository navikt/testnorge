package no.nav.dolly.domain.projection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HendelseIdFragment {

    private Long id;
    private Long bestillingId;
    private String ident;
    private String pdlOrdreStatus;
}
