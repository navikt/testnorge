package no.nav.testnav.dollysearchservice.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ForelderBarnRelasjonModel implements WithMetadata {
    String relatertPersonsIdent;
    String relatertPersonsRolle;
    String minRolleForPerson;
    Metadata metadata;
}
