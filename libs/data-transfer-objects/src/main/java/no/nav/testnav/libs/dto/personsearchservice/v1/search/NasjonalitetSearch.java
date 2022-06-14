package no.nav.testnav.libs.dto.personsearchservice.v1.search;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

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
        String fraflyttingsland;
        String historiskFraflyttingsland;
    }

    @Value
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public class UtflyttingSearch {
        String tilflyttingsland;
        String historiskTilflyttingsland;
    }
}
