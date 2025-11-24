package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.arena;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.arena.testnorge.brukere.NyEndreInnsatsbehov;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EndreInnsatsbehovRequest {

    private String personident;
    private String miljoe;

    private List<NyEndreInnsatsbehov> nyeEndreInnsatsbehov;

}
