package no.nav.dolly.domain.resultset.udistub.model.opphold;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class UtvistMedInnreiseForbudTo {

    private String innreiseForbud;
    private LocalDate innreiseForbudVedtaksDato;
    private String varighet;
}
