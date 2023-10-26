package no.nav.testnav.libs.data.pdlforvalter.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonIDDTO {

    private String ident;
    private String fornavn;
    private String mellomnavn;
    private String etternavn;
}