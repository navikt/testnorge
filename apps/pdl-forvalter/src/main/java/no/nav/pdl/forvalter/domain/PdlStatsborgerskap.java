package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PdlStatsborgerskap extends PdlDbVersjon{

    private String landkode;
    private LocalDateTime gyldigFom;
    private LocalDateTime gyldigTom;
}