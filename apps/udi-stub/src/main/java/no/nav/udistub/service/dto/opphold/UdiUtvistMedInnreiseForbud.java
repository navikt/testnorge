package no.nav.udistub.service.dto.opphold;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.udi.mt_1067_nav_data.v1.JaNeiUavklart;
import no.udi.mt_1067_nav_data.v1.Varighet;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class UdiUtvistMedInnreiseForbud {

    private JaNeiUavklart innreiseForbud;
    private LocalDate innreiseForbudVedtaksDato;
    private Varighet varighet;
}
