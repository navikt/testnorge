package no.nav.dolly.domain.resultset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RsDollyUtvidetBestilling extends RsDollyBestilling {

    private Boolean navSyntetiskIdent;

    @JsonIgnore
    private Boolean ekskluderEksternePersoner;
}
