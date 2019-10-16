package no.nav.dolly.domain.resultset.tpsf;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TpsPerson {

    private String hovedperson;
    private String partner;
    private List<String> barn;

    public List<String> getBarn() {
        if (isNull(barn)) {
            barn = new ArrayList();
        }
        return barn;
    }
}
