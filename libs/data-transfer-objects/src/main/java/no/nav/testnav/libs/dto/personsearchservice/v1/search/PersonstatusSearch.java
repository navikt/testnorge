package no.nav.testnav.libs.dto.personsearchservice.v1.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PersonstatusSearch {
    String status;
}
