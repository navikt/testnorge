package no.nav.registre.testnorge.personsearchservice.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PersonDTO {
    String fornavn;
    String mellomnavn;
    String ettternavn;
    String kjoenn;
    String ident;
    String aktorId;
    String tag;
}

