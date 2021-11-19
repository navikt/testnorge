package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RelasjonDTO {

    private PersonDTO personRelasjonMed;
    private String relasjonTypeNavn;

    public enum ROLLE {PARTNER, EKTEFELLE, MOR, FAR, FOEDSEL, BARN}
}