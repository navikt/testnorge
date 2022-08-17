package no.nav.testnav.apps.skdservice.consumer.response;

import lombok.*;

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
