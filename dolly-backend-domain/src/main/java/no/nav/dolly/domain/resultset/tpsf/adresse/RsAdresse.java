package no.nav.dolly.domain.resultset.tpsf.adresse;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "adressetype")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RsGateadresse.class, name = "GATE"),
        @JsonSubTypes.Type(value = RsMatrikkeladresse.class, name = "MATR")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class RsAdresse {

    @ApiModelProperty(
            position = 0,
            required = true,
            value = "Angir adressetype, GATE eller MATR.\n" +
                    "For gateadresse, inkluder: \n" +
                    " \"gateadresse\": \"string\" Forstås som gatenavn\n" +
                    " \"husnummer\": \"string\" * \n" +
                    " \"gatekode\": \"string\" * Hentes fra adressesøk\n" +
                    "For matrikkeladresse inkluder:\n" +
                    " \"mellomnavn\": \"string\" Forståes som gårdsnavn\n" +
                    " \"gardsnr\": \"string\" *\n" +
                    " \"bruksnr\": \"string\" *\n" +
                    " \"festenr\": \"string\"\n" +
                    " \"undernr\": \"string\""
    )
    private String adressetype;

    @ApiModelProperty(
            position = 1,
            value = "Postnummer på adresse"
    )
    private String postnr;

    @ApiModelProperty(
            position = 1,
            required = true,
            value = "Kommunenummer for adresse"
    )
    private String kommunenr;

    @ApiModelProperty(
            position = 1,
            dataType = "LocalDateTime",
            value = "Flyttedato for adresse. Hvis tomt blir flyttedato identisk med fødselsdato"
    )
    private LocalDateTime flyttedato;

}
