package no.nav.testnav.apps.apptilganganalyseservice.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Builder
@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ItemDTO {
    String name;
    String sha;
}
