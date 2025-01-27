package no.nav.testnav.dollysearchservice.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class SivilstandModel implements WithMetadata{
    String type;
    String relatertVedSivilstand;
    Metadata metadata;
}
