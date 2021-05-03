package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Folkeregistermetadata implements Serializable {

    private LocalDateTime ajourholdstidspunkt;
    private LocalDateTime gyldighetstidspunkt;
    private LocalDateTime opphoerstidspunkt;
}