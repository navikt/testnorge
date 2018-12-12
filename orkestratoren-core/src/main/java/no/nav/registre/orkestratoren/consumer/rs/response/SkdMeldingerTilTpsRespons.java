package no.nav.registre.orkestratoren.consumer.rs.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkdMeldingerTilTpsRespons {

    private int antallSendte = 0;
    private int antallFeilet = 0;
    private List<StatusPaaAvspiltSkdMelding> statusFraFeilendeMeldinger = new ArrayList();
    private List<Long> tpsfIds;

    public SkdMeldingerTilTpsRespons addStatusFraFeilendeMeldinger(StatusPaaAvspiltSkdMelding statusFraFeilendeMeldinger) {
        if (statusFraFeilendeMeldinger == null) {
            this.statusFraFeilendeMeldinger = new ArrayList();
        }
        this.statusFraFeilendeMeldinger.add(statusFraFeilendeMeldinger);
        return this;
    }
}
