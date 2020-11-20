package no.nav.registre.arena.core.consumer.rs.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.NyEndreInnsatsbehov;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.NyEndreInnsatsbehovFeil;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndreInnsatsbehovResponse {

    private List<NyEndreInnsatsbehov> nyeEndreInnsatsbehov;

    private List<NyEndreInnsatsbehovFeil> nyeEndreInnsatsbehovFeilList;
}
