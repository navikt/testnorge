package no.nav.registre.orkestratoren.provider.rs.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import no.nav.registre.orkestratoren.consumer.rs.response.SletteArbeidsforholdResponse;
import no.nav.registre.orkestratoren.consumer.rs.response.SletteArenaResponse;
import no.nav.registre.orkestratoren.consumer.rs.response.SletteInstitusjonsoppholdResponse;
import no.nav.registre.orkestratoren.consumer.rs.response.SletteSkattegrunnlagResponse;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlettedeIdenterResponse {

    private SletteFraAvspillerguppeResponse tpsfStatus;
    private SletteInstitusjonsoppholdResponse instStatus;
    private SletteSkattegrunnlagResponse sigrunStatus;
    private SletteArbeidsforholdResponse aaregStatus;
    private SletteArenaResponse arenaForvalterStatus;
}
