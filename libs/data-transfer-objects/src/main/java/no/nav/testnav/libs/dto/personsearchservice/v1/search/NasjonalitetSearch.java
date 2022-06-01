package no.nav.testnav.libs.dto.personsearchservice.v1.search;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class NasjonalitetSearch {
    String statsborgerskap;
    Boolean innflyttingTilNorge;
    Boolean utflyttingFraNorge;
    InnflyttingSearch innflytting;
    UtflyttingSearch utflytting;

    @Value
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public class InnflyttingSearch {
        List<String> fraflyttingsland;
        List<String> historiskFraflyttingsland;
    }

    @Value
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public class UtflyttingSearch {
        List<String> tilflyttingsland;
        List<String> historiskTilflyttingsland;
    }
}
