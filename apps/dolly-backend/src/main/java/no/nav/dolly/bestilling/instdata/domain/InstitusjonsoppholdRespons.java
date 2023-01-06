package no.nav.dolly.bestilling.instdata.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.resultset.inst.Instdata;
import org.apache.commons.collections4.map.HashedMap;

import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitusjonsoppholdRespons {

    private Map<String, List<Instdata>> institusjonsopphold;

    public Map<String, List<Instdata>> getInstitusjonsopphold() {

        if (isNull(institusjonsopphold)) {
            institusjonsopphold = new HashedMap<>();
        }
        return institusjonsopphold;
    }
}
