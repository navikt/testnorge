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
public class RsPdlUtenlandskIdentifikasjonsnummer {

    private String identifikasjonsnummer;
    private Boolean opphoert;
    private String utstederland;
}
