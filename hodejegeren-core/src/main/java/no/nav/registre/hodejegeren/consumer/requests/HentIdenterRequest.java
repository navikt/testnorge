package no.nav.registre.hodejegeren.consumer.requests;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HentIdenterRequest {
    private IdentType identtype;
    private LocalDate foedtEtter;
    private LocalDate foedtFoer;
    private Kjonn kjoenn;
    private int antall;
    
    public enum IdentType {
        FNR, DNR
    }
    
    public enum Kjonn {
        MANN, KVINNE
    }
}