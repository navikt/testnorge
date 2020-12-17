package no.nav.organisasjonforvalter.provider.rs.responses;

import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BestillingResponse {

    private Set<String> orgnummer;
}
