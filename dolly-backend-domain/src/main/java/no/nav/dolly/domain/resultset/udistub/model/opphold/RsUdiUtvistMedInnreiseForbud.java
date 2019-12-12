package no.nav.dolly.domain.resultset.udistub.model.opphold;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.udistub.model.UdiHarType;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class RsUdiUtvistMedInnreiseForbud {

    private UdiHarType innreiseForbud;
    private LocalDateTime innreiseForbudVedtaksDato;
    private UdiVarighetType varighet;
}
