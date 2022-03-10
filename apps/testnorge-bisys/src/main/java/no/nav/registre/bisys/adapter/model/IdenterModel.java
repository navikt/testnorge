package no.nav.registre.bisys.adapter.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class IdenterModel {
    String ident;
    String gruppe;
}
