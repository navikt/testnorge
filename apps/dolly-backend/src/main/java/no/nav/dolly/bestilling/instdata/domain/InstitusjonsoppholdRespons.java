package no.nav.dolly.bestilling.instdata.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.resultset.inst.Instdata;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitusjonsoppholdRespons {

    private Object resultat;
    private Map<String, List<Instdata>> institusjonsopphold;

    private HttpStatus status;
    private String error;

    public Map<String, List<Instdata>> getInstitusjonsopphold() {

        if (isNull(institusjonsopphold)) {
            institusjonsopphold = new HashedMap<>();
        }
        return institusjonsopphold;
    }
}
