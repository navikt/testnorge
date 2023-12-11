package no.nav.testnav.apps.apptilganganalyseservice.domain.yml.application.v1;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Rule {
    String application;
    String cluster;
    String namespace;
}
