package no.nav.dolly.bestilling.pensjonforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AlderspensjonVedtakDTO extends AlderspensjonVedtakRequest {

    private LocalDate fom;

    private LocalDate datoForrigeGraderteUttak;
    private Integer nyUttaksgrad;

    @Builder.Default
    private List<AlderspensjonVedtakDTO> historikk = new ArrayList<>();
}
