package no.nav.dolly.domain.resultset.kodeverk;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KodeverkAdjusted {
    String name;
    List<KodeAdjusted> koder;
}
