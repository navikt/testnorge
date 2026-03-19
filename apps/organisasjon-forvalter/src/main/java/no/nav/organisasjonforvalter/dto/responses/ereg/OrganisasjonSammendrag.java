package no.nav.organisasjonforvalter.dto.responses.ereg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "Informasjon om organisasjon (i avansert søk)")
@JsonPropertyOrder({
        "organisasjonsnummer",
        "enhetstype",
        "redigertnavn",
        "navnelinje1",
        "navnelinje2",
        "navnelinje3",
        "navnelinje4",
        "navnelinje5",
        "organisasjonsleddOrganisasjonsnummer",
        "juridiskEnhetOrganisasjonsnummer",
        "adresselinje1",
        "adresselinje2",
        "adresselinje3",
        "postnummer",
        "poststed",
        "kommunenummer",
        "kommunenavn",
        "landkode",
        "opphoert"
})
@SuppressWarnings("pmd:TooManyFields")
public class OrganisasjonSammendrag {

    @Schema(description = "Organisasjonsnummer", example = "990983666")
    private String organisasjonsnummer;

    @Schema(description = "Redigert navn", example = "NAV FAMILIE- OG PENSJONSYTELSER OSL")
    private String redigertnavn;

    @Schema(description = "Navnelinje #1", example = "NAV FAMILIE- OG PENSJONSYTELSER")
    private String navnelinje1;

    @Schema(description = "Navnelinje #2")
    private String navnelinje2;

    @Schema(description = "Navnelinje #3")
    private String navnelinje3;

    @Schema(description = "Navnelinje #4")
    private String navnelinje4;

    @Schema(description = "Navnelinje #5")
    private String navnelinje5;

    @Schema(description = "Enhetstype (kodeverk)", example = "BEDR")
    private String enhetstype;

    @Schema(description = "Organisasjonsnummer for organisasjonsledd", example = "889640782")
    private String organisasjonsleddOrganisasjonsnummer;

    @Schema(description = "Organisasjonsnummer for juridisk enhet", example = "983887457")
    private String juridiskEnhetOrganisasjonsnummer;

    @Schema(description = "Adresselinje #1")
    private String adresselinje1;

    @Schema(description = "Adresselinje #2")
    private String adresselinje2;

    @Schema(description = "Adresselinje #3")
    private String adresselinje3;

    @Schema(description = "Postnummer (kodeverk: Postnummer)", example = "0557")
    private String postnummer;

    @Schema(description = "Poststed", example = "Oslo")
    private String poststed;

    @Schema(description = "Kommunenummer (kodeverk: Kommuner)", example = "0301")
    private String kommunenummer;

    @Schema(description = "Kommunenavn", example = "Oslo")
    private String kommunenavn;

    @Schema(description = "Landkode (kodeverk: Landkoder)", example = "JPN")
    private String landkode;

    @Schema(description = "Er organisasjonen opph&oslash;rt?", example = "false")
    private boolean opphoert;
}
