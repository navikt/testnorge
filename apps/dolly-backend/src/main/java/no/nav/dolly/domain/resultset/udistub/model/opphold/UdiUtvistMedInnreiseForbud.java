package no.nav.dolly.domain.resultset.udistub.model.opphold;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.resultset.udistub.model.UdiHarType;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UdiUtvistMedInnreiseForbud {

    private UdiHarType innreiseForbud;
    private LocalDate innreiseForbudVedtaksDato;
    private UdiVarighetType varighet;
}
