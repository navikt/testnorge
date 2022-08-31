package no.nav.testnav.apps.syntsykemeldingapi.domain.pdl;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Metadata {
    Boolean historisk;
}
