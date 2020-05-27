package no.nav.registre.skd.consumer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkdMeldingerTilTpsRespons {

    private int antallSendte;
    private int antallFeilet;
    private List<StatusPaaAvspiltSkdMelding> statusFraFeilendeMeldinger;
}
