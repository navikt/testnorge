package no.nav.testnav.identpool.providers.v1.support;

import lombok.Data;
import no.nav.testnav.identpool.domain.Identtype;

import java.time.LocalDate;

@Data
public class RekvirerIdentRequest {

        private Identtype identtype;
        private LocalDate foedtEtter;
        private LocalDate foedtFoer;
}
