package no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TpsServiceRoutineHentRequest extends TpsServiceRoutineRequest {

    private String aksjonsDato;

    private String aksjonsKode;
    private String aksjonsKode2;

    public void setAksjonsKode(String aksjonsKode) {
        this.aksjonsKode = aksjonsKode.substring(0, 1);
        this.aksjonsKode2 = aksjonsKode.substring(1);
    }

}
