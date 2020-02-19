package no.nav.registre.spion.domain;
import java.util.ArrayList;
import java.util.Hashtable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vedtak {

    private String ytelse;
    private Hashtable<String, String> periode;
    private String fnr;
    //    private String arbeidsforholdId;
    private String vnr;
    private String vedtaksstatus;
    private String fritakFraAGP;
    private int sykemeldingsgrad;
    private int refusjonsbelopPerPeriode;
    private int dagsats;
    private String maksDato;
    private String ferie;

}
