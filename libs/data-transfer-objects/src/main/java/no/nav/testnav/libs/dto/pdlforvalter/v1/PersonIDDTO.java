package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PersonIDDTO {

    private String ident;
    private String fornavn;
    private String mellomnavn;
    private String etternavn;
}