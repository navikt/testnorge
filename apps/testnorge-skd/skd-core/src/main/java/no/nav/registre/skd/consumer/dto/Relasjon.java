package no.nav.registre.skd.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Relasjon {

    String kortnavn;
    String datoDo;
    String typeRelBeskr;
    String mellomnavn;
    String etternavn;
    Integer adresseStatus;
    String adrStatusBeskr;
    String spesregType;
    String fornavn;
    String fnrRelasjon;
    String typeRelasjon;

}
