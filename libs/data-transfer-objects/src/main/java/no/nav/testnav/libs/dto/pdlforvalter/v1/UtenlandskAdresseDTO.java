package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UtenlandskAdresseDTO implements Serializable {

    private String adressenavnNummer;
    private String boenhet;
    private String bySted;
    private String bygning;
    private String bygningEtasjeLeilighet;
    private String distriktnavn;
    private String etasjenummer;
    private String landkode;
    private String postboksNummerNavn;
    private String postkode;
    private String region;
    private String regionDistriktOmraade;
}