package no.nav.testnav.libs.dto.ereg.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "Informasjon om adresse")
@JsonPropertyOrder({
        "adresselinje1",
        "adresselinje2",
        "adresselinje3",
        "adresselinje4",
        "adresselinje5",
        "postnummer",
        "poststed",
        "landkode",
        "kommunenummer",
        "vegadresseId",
        "bruksperiode",
        "gyldighetsperiode"
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @Type(value = Postadresse.class, name = "Postadresse"),
        @Type(value = Forretningsadresse.class, name = "Forretningsadresse")
})
public abstract class Adresse {

    private Bruksperiode bruksperiode = new Bruksperiode();

    private Gyldighetsperiode gyldighetsperiode = new Gyldighetsperiode();

    @Schema(description = "Adresselinje #1")
    private String adresselinje1;

    @Schema(description = "Adresselinje #2")
    private String adresselinje2;

    @Schema(description = "Adresselinje #3")
    private String adresselinje3;

    @Schema(description = "Postnummer (kodeverk: Postnummer)", example = "0557")
    private String postnummer;

    @Schema(description = "Poststed - brukes i forbindelse med utenlandske adresser")
    private String poststed;

    @Schema(description = "Kommunenummer (kodeverk: Kommuner)", example = "0301")
    private String kommunenummer;

    @Schema(description = "Landkode (kodeverk: Landkoder)", example = "JPN")
    private String landkode;
}
