package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlStatsborgerskap extends PdlDbVersjon{

    private String kilde;
    private String landkode;
    private LocalDate gyldigFom;
    private LocalDate gyldigTom;
}