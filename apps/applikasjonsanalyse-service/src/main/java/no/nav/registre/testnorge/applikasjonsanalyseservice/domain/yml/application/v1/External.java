package no.nav.registre.testnorge.applikasjonsanalyseservice.domain.yml.application.v1;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class External {
    String host;
}
