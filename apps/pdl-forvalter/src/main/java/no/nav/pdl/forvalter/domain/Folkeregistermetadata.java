package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Folkeregistermetadata implements Serializable {

    private LocalDateTime ajourholdstidspunkt;
    private LocalDateTime gyldighetstidspunkt;
    private LocalDateTime opphoerstidspunkt;
}