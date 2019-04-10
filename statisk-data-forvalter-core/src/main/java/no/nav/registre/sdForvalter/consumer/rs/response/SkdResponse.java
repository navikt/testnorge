package no.nav.registre.sdForvalter.consumer.rs.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

    @ToString
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private class StatusPaaAvspiltSkdMelding {

        @JsonProperty("foedselsnummer")
        private String foedselsnummer;

        @JsonProperty("sekvensnummer")
        private Long sekvensnummer;

        @JsonProperty("status")
        private String status;
    }

}


