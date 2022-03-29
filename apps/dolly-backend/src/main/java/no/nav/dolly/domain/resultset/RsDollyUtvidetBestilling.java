package no.nav.dolly.domain.resultset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfUtvidetBestilling;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsDollyUtvidetBestilling extends RsDollyBestilling {

    private RsTpsfUtvidetBestilling tpsf;

    private Boolean navSyntetiskIdent;

    private String beskrivelse;

    private List<Tags> tags;

    @JsonIgnore
    private Boolean ekskluderEksternePersoner;
}
