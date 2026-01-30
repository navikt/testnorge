package no.nav.testnav.libs.dto.skattekortservice.v1;

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