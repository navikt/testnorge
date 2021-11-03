package no.nav.dolly.domain.resultset.kodeverk;

import static java.util.Objects.isNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KodeverkAdjusted {

    private String name;
    private List<KodeAdjusted> koder;

    public List<KodeAdjusted> getKoder() {
        if (isNull(koder)) {
            koder = new ArrayList<>();
        }
        return koder;
    }
}
