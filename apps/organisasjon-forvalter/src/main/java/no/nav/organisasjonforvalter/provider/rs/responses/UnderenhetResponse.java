package no.nav.organisasjonforvalter.provider.rs.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UnderenhetResponse {

    private String orgnummer;
    private String orgnavn;
    private String enhetstype;
}
