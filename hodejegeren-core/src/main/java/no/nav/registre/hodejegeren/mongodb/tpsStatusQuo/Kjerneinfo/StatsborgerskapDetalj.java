package no.nav.registre.hodejegeren.mongodb.tpsStatusQuo.Kjerneinfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsborgerskapDetalj {

    private String kodeStatsborgerskapBeskr;
    private String sbSystem;
    private String sbTidspunkt;
    private String kodeStatsborgerskap;
    private String sbSaksbehandler;
    private String datoStatsborgerskap;
}
