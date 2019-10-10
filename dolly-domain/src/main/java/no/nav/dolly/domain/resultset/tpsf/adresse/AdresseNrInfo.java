package no.nav.dolly.domain.resultset.tpsf.adresse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdresseNrInfo {
    
    @NonNull
    private AdresseNr nummertype;
    
    @NonNull
    private String nummer;
    
    public enum AdresseNr {
        KOMMUNENR, POSTNR
    }
}
