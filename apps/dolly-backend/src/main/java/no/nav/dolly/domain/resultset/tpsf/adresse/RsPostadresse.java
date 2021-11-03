package no.nav.dolly.domain.resultset.tpsf.adresse;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class RsPostadresse {

    @Schema(required = true,
            description = "Adresselinje 1 fritekst postadresse eller boadresse")
    private String postLinje1;

    @Schema(description = "Adresselinje 2 fritekst postadresse eller boadresse")
    private String postLinje2;

    @Schema(description = "Adresselinje 3 fritekst postadresse eller boadresse")
    private String postLinje3;

    @Schema(description = "Landkode i hht kodeverk 'Landkoder'. "
            + "Tomt felt eller NOR betyr Norge og siste benyttede adresselinje 1, 2 eller 3 må starte med postnummer")
    private String postLand;

    @JsonIgnore
    public boolean isValid() {
        int count = 0;
        if (isNotBlank(postLinje1)) {
            count++;
        }
        if (isNotBlank(postLinje2)) {
            count++;
        }
        if (isNotBlank(postLinje3)) {
            count++;
        }
        return count > 1;
    }

    @JsonIgnore
    public boolean isNorsk() {
        return (isBlank(postLand) || "NOR".equals(postLand)) && isValid();
    }

    @JsonIgnore
    public boolean isUtenlandsk() {
        return isNotBlank(postLand) && !"NOR".equals(postLand) && isValid();
    }
}
