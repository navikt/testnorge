package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PdlUtenlandskAdresse implements Serializable {

    private String adressenavnNummer;
    private String bySted;
    private String bygningEtasjeLeilighet;
    private String landkode;
    private String postboksNummerNavn;
    private String postkode;
    private String regionDistriktOmraade;
}