package no.nav.testnav.libs.dto.personsearchservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class IdentdataDTO {

    private String ident;
    private NavnDTO navn;
}
