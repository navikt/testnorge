package no.nav.testnav.libs.dto.sykemelding.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
public class PasientDTO {
    private String ident;
    private String fornavn;
    private String mellomnavn;
    private String etternavn;
    private AdresseDTO adresse;
    private LocalDate foedselsdato;
    private String navKontor;
    private String telefon;
}
