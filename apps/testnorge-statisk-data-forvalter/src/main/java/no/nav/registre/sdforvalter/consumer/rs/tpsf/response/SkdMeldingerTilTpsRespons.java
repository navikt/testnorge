package no.nav.registre.sdforvalter.consumer.rs.tpsf.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkdMeldingerTilTpsRespons {

    private int antallSendte;
    private int antallFeilet;
    private List<StatusPaaAvspiltSkdMelding> statusFraFeilendeMeldinger;


    public List<StatusPaaAvspiltSkdMelding> getStatusFraFeilendeMeldinger() {
        if (isNull(statusFraFeilendeMeldinger)){
            statusFraFeilendeMeldinger = new ArrayList<>();
        }
        return statusFraFeilendeMeldinger;
    }

    public List<String> getFailedStatus() {
        return getStatusFraFeilendeMeldinger().parallelStream().map(StatusPaaAvspiltSkdMelding::toString).toList();
    }

}
