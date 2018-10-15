package no.nav.dolly.domain.resultset.kodeverk;

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
    String name;
    List<KodeAdjusted> koder = new ArrayList<>();
}
