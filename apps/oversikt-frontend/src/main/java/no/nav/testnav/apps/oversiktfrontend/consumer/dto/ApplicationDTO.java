package no.nav.testnav.apps.oversiktfrontend.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ApplicationDTO {
    String name;
    String namespace;
    String cluster;
}