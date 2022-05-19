package no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DagpengesoknadDTO {

    private String personident;
    private String miljoe;
    private Soknad nyeMottaDagpengesoknad;

    @Data
    public class Soknad {
        private String enhetsnr;
        private String kommentar;
    }

}
