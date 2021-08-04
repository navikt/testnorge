package no.nav.testnav.libs.dto.hodejegeren.v1;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PersondataDTO {
    private final String fnr;
    private final String fornavn;
    private final String mellomnavn;
    private final String etternavn;
}
