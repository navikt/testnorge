package no.nav.registre.orkestratoren.consumer.rs.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SkdMeldingerTilTpsRespons {

    private int antallSendte;
    private int antallFeilet;
    private List<StatusPaaAvspiltSkdMelding> statusFraFeilendeMeldinger;
}
