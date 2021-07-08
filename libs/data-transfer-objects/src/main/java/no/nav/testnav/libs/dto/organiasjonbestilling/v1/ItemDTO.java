package no.nav.testnav.libs.dto.organiasjonbestilling.v1;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ItemDTO {
    Long id;
    Status status;
}
