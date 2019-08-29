package no.nav.dolly.domain.resultset.udistub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.nav.dolly.domain.resultset.udistub.model.ArbeidsadgangTo;
import no.nav.dolly.domain.resultset.udistub.model.opphold.OppholdStatusTo;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RsUdiPersonData {

    private ArbeidsadgangTo arbeidsadgang;
    private OppholdStatusTo oppholdStatus;
}
