package no.nav.dolly.domain.resultset.udistub.model.opphold;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum {

    private UdiAvslagEllerBortfall avslagEllerBortfall;
    private UdiOverigIkkeOppholdKategoriType ovrigIkkeOppholdsKategoriArsak;
    private UdiUtvistMedInnreiseForbud utvistMedInnreiseForbud;
}