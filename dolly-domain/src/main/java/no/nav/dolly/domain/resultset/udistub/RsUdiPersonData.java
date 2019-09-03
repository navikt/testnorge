package no.nav.dolly.domain.resultset.udistub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.nav.dolly.domain.resultset.udistub.model.UdiArbeidsadgang;
import no.nav.dolly.domain.resultset.udistub.model.opphold.OppholdStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RsUdiPersonData {

    private UdiArbeidsadgang arbeidsadgang;
    private OppholdStatus oppholdStatus;
}
