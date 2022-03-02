package no.nav.testnav.libs.dto.personsearchservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ForelderDTO {
    String ident;
    String rolle;
}
