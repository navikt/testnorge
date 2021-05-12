package no.nav.pdl.forvalter.domain;

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
public class PdlUtenlandskAdresse implements Serializable {

    private String adressenavnNummer;
    private String bySted;
    private String bygningEtasjeLeilighet;
    private String landkode;
    private String postboksNummerNavn;
    private String postkode;
    private String regionDistriktOmraade;
}