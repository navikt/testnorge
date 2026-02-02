package no.nav.dolly.bestilling.skattekort.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trekkprosent {

    private Trekkode trekkode;

    private Integer prosentsats;
    private Integer antallMaanederForTrekk;
}
