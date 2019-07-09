package no.nav.registre.ereg.provider.rs.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
@Getter
@AllArgsConstructor
public class Knytning {

    private String type = "BEDRNSSY";
    private String ansvarsandel = "";
    private String fratreden = "";
    private String orgnr;
    private String valgtAv = "";
    private String korrektOrgNr = "";
}
