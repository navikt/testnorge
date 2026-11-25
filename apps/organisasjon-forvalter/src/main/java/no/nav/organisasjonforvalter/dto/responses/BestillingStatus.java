package no.nav.organisasjonforvalter.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BestillingStatus {

    private String orgnummer;
    private String miljoe;
    private StatusDTO status;
    private String feilmelding;
}
