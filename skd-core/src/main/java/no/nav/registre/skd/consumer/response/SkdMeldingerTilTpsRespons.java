package no.nav.registre.skd.consumer.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    public void addTpsfId(Long id) {
        if(tpsfIds == null) {
            tpsfIds = new ArrayList<>();
        }
        tpsfIds.add(id);
    }
}
