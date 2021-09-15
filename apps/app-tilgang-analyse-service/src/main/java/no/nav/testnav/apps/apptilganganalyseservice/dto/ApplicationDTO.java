package no.nav.testnav.apps.apptilganganalyseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
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


