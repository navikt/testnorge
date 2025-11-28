package no.nav.dolly.domain.resultset.pdldata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlPersondata {

    private PdlPerson opprettNyPerson;
    private PersonDTO person;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PdlPerson {

        private Identtype identtype;

        private LocalDateTime foedtEtter;
        private LocalDateTime foedtFoer;
        private Integer alder;
        private Boolean syntetisk;
        private Boolean id2032;
    }
}
