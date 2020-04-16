package no.nav.dolly.domain.resultset.tpsf;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TpsfIdenterMiljoer {

    private List<String> identer;
    private List<String> miljoer;

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
