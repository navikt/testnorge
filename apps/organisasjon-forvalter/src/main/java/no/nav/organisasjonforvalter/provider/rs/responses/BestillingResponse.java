package no.nav.organisasjonforvalter.provider.rs.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BestillingResponse {

    private Set<String> orgnummer;
}
