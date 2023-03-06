package no.nav.dolly.domain.resultset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RsDollyUtvidetBestilling extends RsDollyBestilling {

    private Boolean navSyntetiskIdent;

    private String beskrivelse;

    @JsonIgnore
    private Boolean ekskluderEksternePersoner;
}
