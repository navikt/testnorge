package no.nav.testnav.apps.profilapi.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ProfileDTO {
    String displayName;
    String mail;
    String officeLocation;
}
