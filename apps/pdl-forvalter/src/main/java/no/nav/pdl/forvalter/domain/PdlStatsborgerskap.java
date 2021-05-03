package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlStatsborgerskap extends PdlDbVersjon{

    private String kilde;
    private String landkode;
    private LocalDateTime gyldigFom;
    private LocalDateTime gyldigTom;
}