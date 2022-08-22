package no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NyeDagpengerFeil {
    private String personident;

    private String miljoe;

    @JsonAlias({"nyMottaDagpengesoknadFeilstatus", "nyMottaDagpengevedtakFeilstatus"})
    private String status;

    private String melding;
}
