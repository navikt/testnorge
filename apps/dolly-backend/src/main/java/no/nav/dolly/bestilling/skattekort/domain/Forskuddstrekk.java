package no.nav.dolly.bestilling.skattekort.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Forskuddstrekk {

    private Frikort frikort;
    private Trekktabell trekktabell;
    private Trekkprosent trekkprosent;

}
