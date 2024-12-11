package no.nav.testnav.libs.dto.sykemelding.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HelsepersonellDTO {

    private String ident;
    private String fornavn;
    private String mellomnavn;
    private String etternavn;
    private String hprId;
    private String samhandlerType;
}
