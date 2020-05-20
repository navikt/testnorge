package no.nav.registre.ereg.provider.rs.request;

import lombok.*;

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

