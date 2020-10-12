package no.nav.registre.skd.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Relasjon {

    private final String kortnavn;
    private final String datoDo;
    private final String typeRelBeskr;
    private final String mellomnavn;
    private final String etternavn;
    private final Integer adresseStatus;
    private final String adrStatusBeskr;
    private final String spesregType;
    private final String fornavn;
    private final String fnrRelasjon;
    private final String typeRelasjon;

}
