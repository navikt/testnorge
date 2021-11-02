package no.nav.dolly.domain.resultset.tpsf.adresse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonTypeName("Gateadresse")
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoGateadresse extends BoAdresse {

    private String gateadresse;

    private String husnummer;

    private String gatekode;

    @Override
    public String getAdressetype() {
        return "GATE";
    }
}
