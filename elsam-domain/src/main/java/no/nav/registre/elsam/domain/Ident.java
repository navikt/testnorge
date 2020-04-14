package no.nav.registre.elsam.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ident {

    @NonNull
    private String fnr;

    private String fornavn;
    private String mellomnavn;
    private String etternavn;
    private Adresse adresse;
    private String navKontor;
    private String telefon;
    private Arbeidsgiver arbeidsgiver;
}
