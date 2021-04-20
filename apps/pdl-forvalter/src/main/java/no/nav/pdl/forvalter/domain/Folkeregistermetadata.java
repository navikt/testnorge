package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Folkeregistermetadata implements Serializable {

    private LocalDate ajourholdstidspunkt;
    private LocalDate gyldighetstidspunkt;
    private LocalDate opphoerstidspunkt;
}