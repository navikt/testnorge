package no.nav.pdl.forvalter.dto;

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
public class PdlStatsborgerskap {

    private String kilde;
    private String landkode;
    private LocalDate gyldigFom;
    private LocalDate gyldigTom;
}