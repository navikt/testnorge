package no.nav.skattekortservice.dto.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForskuddstrekkDTO {

    private TrekkodeType trekkode;

    private Integer frikortBeloep;

    private String tabell;

    private Double prosentSats;

    private Double antallMndForTrekk;
}
