package no.nav.testnav.apps.apptilganganalyseservice.domain.yml.application.v1;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Inbound {
    List<Rule> rules;
}
