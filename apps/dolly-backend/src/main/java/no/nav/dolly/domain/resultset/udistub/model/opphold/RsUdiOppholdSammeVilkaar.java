package no.nav.dolly.domain.resultset.udistub.model.opphold;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPeriode;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsUdiOppholdSammeVilkaar {

    private LocalDateTime oppholdSammeVilkaarEffektuering;
    private RsUdiPeriode oppholdSammeVilkaarPeriode;
    private UdiOppholdstillatelseType oppholdstillatelseType;
    private LocalDateTime oppholdstillatelseVedtaksDato;
}