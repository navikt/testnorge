package no.nav.dolly.bestilling.skattekort.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trekktabell {

    private Trekkode trekkode;

    private String tabellnummer;
    private Integer prosentsats;
    private Integer antallMaanederForTrekk;
}
