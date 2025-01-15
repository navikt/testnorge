package no.nav.testnav.dollysearchservice.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PersonstatusModel implements WithMetadata{
    String status;
    String forenkletStatus;
    Metadata metadata;
    FolkeregisterMetadata folkeregistermetadata;
}
