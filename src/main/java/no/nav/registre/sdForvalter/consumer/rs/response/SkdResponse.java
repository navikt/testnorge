package no.nav.registre.sdForvalter.consumer.rs.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkdResponse {

    private int antallSendte = 0;
    private int antallFeilet = 0;
    private List<StatusPaaAvspiltSkdMelding> statusFraFeilendeMeldinger = new ArrayList<>();
    private List<Long> tpsfIds;

    public List<String> getFailedStatus() {
        return statusFraFeilendeMeldinger.parallelStream().map(StatusPaaAvspiltSkdMelding::toString).collect(Collectors.toList());
    }

}


