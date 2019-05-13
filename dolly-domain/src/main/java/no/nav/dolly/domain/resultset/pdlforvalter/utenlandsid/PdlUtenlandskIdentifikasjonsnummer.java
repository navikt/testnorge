package no.nav.dolly.domain.resultset.pdlforvalter.utenlandsid;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlUtenlandskIdentifikasjonsnummer {

    private LocalDate gyldigFom;
    private LocalDate gyldigTom;
    private String ident;
    private String idnumertype;
    private String idnummer;
    private String kilde;
    private String utstederland;
}
