package no.nav.registre.skd.consumer.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    private String rekvirertAv;

    public enum IdentType {
        FNR, DNR
    }

    public enum Kjonn {
        MANN, KVINNE
    }
}