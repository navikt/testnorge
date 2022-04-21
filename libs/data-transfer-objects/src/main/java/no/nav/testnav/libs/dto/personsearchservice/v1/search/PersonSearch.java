package no.nav.testnav.libs.dto.personsearchservice.v1.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

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
    List<String> excludeTags;
    String kjoenn;
    String randomSeed;
    Boolean kunLevende;
    List<String> identer;
    FoedselSearch foedsel;
    SivilstandSearch sivilstand;
    NasjonalitetSearch nasjonalitet;
    AlderSearch alder;
    IdentifikasjonSearch identifikasjon;
    RelasjonSearch relasjoner;
    PersonstatusSearch personstatus;
    AdresserSearch adresser;
}