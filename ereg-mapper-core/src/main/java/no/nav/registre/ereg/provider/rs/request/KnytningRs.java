package no.nav.registre.ereg.provider.rs.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class KnytningRs {

    private String type = "BEDRNSSY";
    private String ansvarsandel;
    private String fratreden;
    private String orgnr;
    private String valgtAv;
    private String korrektOrgNr;
}

