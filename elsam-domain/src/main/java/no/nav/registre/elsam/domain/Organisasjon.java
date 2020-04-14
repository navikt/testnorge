package no.nav.registre.elsam.domain;

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
public class Organisasjon {

    private String navn;
    private String herId;
    private String orgNr;
    private Adresse adresse;
}
