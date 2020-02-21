package no.nav.registre.spion.domain;
import java.util.Hashtable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Vedtak {

    private final String ytelse;
    private final Hashtable<String, String> periode;
    private final String fnr;
    private final String vnr;
    private final String vedtaksstatus;
    private final String fritakFraAGP;
    private final int sykemeldingsgrad;
    private final int refusjonsbelopPerPeriode;
    private final int dagsats;
    private final int dagerIgjenTilMaksDato;
    private final List<Hashtable<String, String>> ferie;

//TODO: private String arbeidsforholdId?;

}
