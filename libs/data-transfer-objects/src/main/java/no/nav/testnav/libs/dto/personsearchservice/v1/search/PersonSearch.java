package no.nav.testnav.libs.dto.personsearchservice.v1.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PersonSearch {
    Integer page;
    Integer pageSize;
    Integer terminateAfter;
    String tag;
    List<String> tags;
    List<String> excludeTags;
    String kjoenn;
    String randomSeed;
    Boolean kunLevende;
    List<String> identer;
    FoedselsdatoSearch foedselsdato;
    SivilstandSearch sivilstand;
    NasjonalitetSearch nasjonalitet;
    AlderSearch alder;
    IdentifikasjonSearch identifikasjon;
    RelasjonSearch relasjoner;
    PersonstatusSearch personstatus;
    AdresserSearch adresser;
}