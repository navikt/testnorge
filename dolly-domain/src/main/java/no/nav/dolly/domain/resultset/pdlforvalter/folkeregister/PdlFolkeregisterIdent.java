package no.nav.dolly.domain.resultset.pdlforvalter.folkeregister;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.IdentType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlFolkeregisterIdent {

    private LocalDate gyldigFom;
    private String idnummer;
    private String kilde;
    private String status;
    private IdentType type;
}
