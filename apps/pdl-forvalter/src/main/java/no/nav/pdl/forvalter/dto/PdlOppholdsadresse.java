package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PdlOppholdsadresse extends PdlAdresse {

    private OppholdAnnetSted oppholdAnnetSted;
    private UtenlandskAdresse utenlandskAdresse;
    private PdlMatrikkeladresse matrikkeladresse;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UtenlandskAdresse {

        private String adressenavnNummer;
        private String boenhet;
        private String bySted;
        private String bygning;
        private String bygningEtasjeLeilighet;
        private String distriktsnavn;
        private String etasjenummer;
        private String landkode;
        private String postboksNummerNavn;
        private String postkode;
        private String region;
        private String regionDistriktOmraade;
    }
}
