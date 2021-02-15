package no.nav.dolly.bestilling.pdlforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Folkeregistermetadata {

    private LocalDate ajourholdstidspunkt;
    private LocalDate gyldighetstidspunkt;
    private LocalDate opphoerstidspunkt;
}