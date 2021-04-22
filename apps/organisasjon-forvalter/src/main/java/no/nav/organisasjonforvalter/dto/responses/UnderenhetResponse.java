package no.nav.organisasjonforvalter.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UnderenhetResponse {

    private String orgnummer;
    private String orgnavn;
    private String enhetstype;
}
