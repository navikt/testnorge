package no.nav.pdl.forvalter.dto;

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
    private Boolean gjeldende;
    private LocalDate gyldighetstidspunkt;
    private LocalDate opphoerstidspunkt;
}