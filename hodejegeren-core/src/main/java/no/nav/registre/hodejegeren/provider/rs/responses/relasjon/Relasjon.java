package no.nav.registre.hodejegeren.provider.rs.responses.relasjon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Relasjon {

    private String kortnavn;
    private String datoDo;
    private String typeRelBeskr;
    private String mellomnavn;
    private String etternavn;
    private int adresseStatus;
    private String adrStatusBeskr;
    private String spesregType;
    private String fornavn;
    private String fnrRelasjon;
    private String typeRelasjon;
}
