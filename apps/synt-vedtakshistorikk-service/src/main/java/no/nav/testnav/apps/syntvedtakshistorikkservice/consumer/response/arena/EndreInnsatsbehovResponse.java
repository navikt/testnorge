package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.arena;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.NyEndreInnsatsbehov;
import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.NyEndreInnsatsbehovFeil;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndreInnsatsbehovResponse {

    @JsonProperty("nyeEndreInnsatsbehov")
    private List<NyEndreInnsatsbehov> nyeEndreInnsatsbehov;

    @JsonProperty("nyeEndreInnsatsbehovFeilList")
    private List<NyEndreInnsatsbehovFeil> nyeEndreInnsatsbehovFeilList;
}
