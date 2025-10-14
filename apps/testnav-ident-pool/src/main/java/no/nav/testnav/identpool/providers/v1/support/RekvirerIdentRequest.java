package no.nav.testnav.identpool.providers.v1.support;

import lombok.Data;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Kjoenn;

import java.time.LocalDate;

@Data
public class RekvirerIdentRequest {

        private Identtype identtype;
        private LocalDate foedtEtter;
        private LocalDate foedtFoer;
        private Kjoenn kjoenn;
        private Boolean syntetisk;
        private Boolean pid2032;
}
