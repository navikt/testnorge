package no.nav.registre.ereg.provider.rs.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class Kapital {

    private String valuttakode;
    //Ledende 0 kapitalverdi
    private String kapital;
    //Ledende 0
    private String kapitalInnbetalt;
    //Fritt format
    private String kapitalBundet;
    //Flere records hvor kun fritekst er forskjellig, max 70 tegn * 4
    private String fritekst;
}
