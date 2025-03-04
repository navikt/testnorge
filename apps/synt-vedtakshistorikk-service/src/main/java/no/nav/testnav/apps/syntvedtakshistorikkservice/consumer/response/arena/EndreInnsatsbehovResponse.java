package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.arena;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.libs.dto.arena.testnorge.brukere.NyEndreInnsatsbehov;
import no.nav.testnav.libs.dto.arena.testnorge.brukere.NyEndreInnsatsbehovFeil;

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
