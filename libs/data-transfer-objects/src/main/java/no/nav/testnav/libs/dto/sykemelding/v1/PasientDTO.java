package no.nav.testnav.libs.dto.sykemelding.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
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
