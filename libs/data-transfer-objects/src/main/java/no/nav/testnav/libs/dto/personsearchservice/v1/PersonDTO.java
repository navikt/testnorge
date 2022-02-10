package no.nav.testnav.libs.dto.personsearchservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PersonDTO {
    String fornavn;
    String mellomnavn;
    String etternavn;
    String kjoenn;
    String ident;
    String aktorId;
    List<String> tags;
    FoedselDTO foedsel;
    SivilstandDTO sivilstand;
    StatsborgerskapDTO statsborgerskap;
    UtfyttingFraNorgeDTO utfyttingFraNorge;
    InnflyttingTilNorgeDTO innfyttingTilNorge;
}