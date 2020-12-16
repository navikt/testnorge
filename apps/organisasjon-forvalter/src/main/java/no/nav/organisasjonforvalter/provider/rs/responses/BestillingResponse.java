package no.nav.organisasjonforvalter.provider.rs.responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BestillingResponse {

    private List<String> orgnummer;
}
