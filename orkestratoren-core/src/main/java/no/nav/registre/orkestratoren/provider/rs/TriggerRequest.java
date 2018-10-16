package no.nav.registre.orkestratoren.provider.rs;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class TriggerRequest {
    private int antallSkdMeldinger;
    private List<String> miljoer;
    private List<String> aarsakskoder;
}
