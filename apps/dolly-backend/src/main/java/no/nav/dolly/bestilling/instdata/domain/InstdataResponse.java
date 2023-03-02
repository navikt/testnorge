package no.nav.dolly.bestilling.instdata.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.inst.Instdata;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstdataResponse {

    private HttpStatus status;
    private String personident;
    private Map<String, List<Instdata>> institusjonsopphold;
    private Instdata instdata;
    private String feilmelding;

    private List<String> environments;

    public Map<String, List<Instdata>> getInstitusjonsopphold() {

        if (isNull(institusjonsopphold)) {
            institusjonsopphold = new HashedMap<>();
        }
        return institusjonsopphold;
    }

    public List<String> getEnvironments() {

        if (isNull(environments)) {
            environments = new ArrayList<>();
        }
        return environments;
    }
}
