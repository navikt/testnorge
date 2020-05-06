package no.nav.dolly.bestilling.bregstub.domain;

import static java.util.Objects.isNull;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KodeRoller {

    private Map<String, String> roller;

    public Map<String, String> getRoller() {
        if (isNull(roller)) {
            roller = new HashMap<>();
        }
        return roller;
    }
}
