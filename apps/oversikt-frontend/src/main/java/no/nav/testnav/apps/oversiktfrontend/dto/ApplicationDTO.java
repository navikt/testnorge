package no.nav.testnav.apps.oversiktfrontend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ApplicationDTO {
    String cluster;
    String namespace;
    String name;
}
