package no.nav.dolly.domain.resultset.tpsf.adresse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class RsGateadresse extends RsAdresse {

    @Schema(required = true,
            description = "Gatenavn på adresse")
    private String gateadresse;

    @Schema(description = "Husnummer på adresse")
    private String husnummer;

    @Schema(required = true,
            description = "Gatekode fra adressesøket")
    private String gatekode;

    @Override
    public String getAdressetype() {
        return "GATE";
    }
}
