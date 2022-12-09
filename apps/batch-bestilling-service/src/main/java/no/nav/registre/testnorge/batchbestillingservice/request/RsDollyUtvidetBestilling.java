package no.nav.registre.testnorge.batchbestillingservice.request;

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

    private Object tpsf;

    private Boolean navSyntetiskIdent;

    private String beskrivelse;

    @JsonIgnore
    private Boolean ekskluderEksternePersoner;
}
