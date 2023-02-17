package no.nav.dolly.domain.resultset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsDollyUtvidetBestilling extends RsDollyBestilling {

    private Boolean navSyntetiskIdent;

    private String beskrivelse;

    @JsonIgnore
    private Boolean ekskluderEksternePersoner;
}
