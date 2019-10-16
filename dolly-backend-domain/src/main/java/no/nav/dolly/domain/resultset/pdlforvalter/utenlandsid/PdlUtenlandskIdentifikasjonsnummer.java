package no.nav.dolly.domain.resultset.pdlforvalter.utenlandsid;

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

    private String identifikasjonsnummer;
    private String kilde;
    private Boolean opphoert;
    private String utstederland;
}
