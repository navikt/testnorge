package no.nav.testnav.apps.apptilganganalyseservice.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ApplicationDTO {
    String name;
    String namespace;
}


