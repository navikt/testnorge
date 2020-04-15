package no.nav.dolly.domain.resultset.udistub.model.opphold;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonInclude;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UdiUtvistMedInnreiseForbud {

    private UdiHarType innreiseForbud;
    private LocalDate innreiseForbudVedtaksDato;
    private UdiVarighetType varighet;
}
