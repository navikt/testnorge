package no.nav.registre.orkestratoren.consumer.rs.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class AvspillingResponse {

    private int antallSendte = 0;
    private int antallFeilet = 0;
    private List<StatusPaaAvspiltSkdMelding> statusFraFeilendeMeldinger = new ArrayList();

    public void incrementAntallSendte() {
        this.antallSendte++;
    }

    public void incrementAntallFeilet() {
        this.antallFeilet++;
    }

    public AvspillingResponse addStatusFraFeilendeMeldinger(StatusPaaAvspiltSkdMelding statusFraFeilendeMeldinger) {
        this.statusFraFeilendeMeldinger.add(statusFraFeilendeMeldinger);
        return this;
    }
}
