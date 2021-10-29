package no.nav.dolly.domain.resultset.tpsf.adresse;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdresseNrInfo {

    public enum AdresseNr {KOMMUNENR, POSTNR}

    @Schema(required = true,
            description = "Angir om backend generert boadresse skal baseres på KOMMUNENR eller POSTNR. Default er random for hele landet.")
    private AdresseNr nummertype;

    @Schema(required = true,
            description = "Angir verdi for kommune- eller postnummer")
    private String nummer;
}
