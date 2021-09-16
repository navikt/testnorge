package no.nav.testnav.apps.apptilganganalyseservice.domain.yml.application.v1;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class KindApplikasjon {
    Metadata metadata;
    String kind;
    Spec spec;
}
