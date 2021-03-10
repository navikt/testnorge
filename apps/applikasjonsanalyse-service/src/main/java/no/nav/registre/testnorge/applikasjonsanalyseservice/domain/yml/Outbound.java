package no.nav.registre.testnorge.applikasjonsanalyseservice.domain.yml;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Outbound {
    List<Rule> rules;
    List<External> external;
}