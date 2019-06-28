package no.nav.dolly.domain.resultset;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class TpsfIdenterMiljoer {

    List<String> identer;
    List<String> miljoer;

    public List<String> getIdenter() {
        if (identer == null) {
            identer = new ArrayList<>();
        }
        return identer;
    }

    public List<String> getMiljoer() {
        if (miljoer == null) {
            miljoer = new ArrayList<>();
        }
        return miljoer;
    }
}
