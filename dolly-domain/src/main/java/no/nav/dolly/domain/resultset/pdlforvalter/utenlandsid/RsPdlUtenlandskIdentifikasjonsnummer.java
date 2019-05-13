package no.nav.dolly.domain.resultset.pdlforvalter.utenlandsid;

import java.time.LocalDateTime;

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
public class RsPdlUtenlandskIdentifikasjonsnummer {

    private LocalDateTime gyldigFom;
    private LocalDateTime gyldigTom;
    private String idnumertype;
    private String idnummer;
    private String utstederland;
}
