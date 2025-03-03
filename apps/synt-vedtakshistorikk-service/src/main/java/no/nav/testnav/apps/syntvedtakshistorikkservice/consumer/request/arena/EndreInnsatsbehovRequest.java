package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.arena;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.arena.testnorge.brukere.NyEndreInnsatsbehov;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EndreInnsatsbehovRequest {

    private String personident;
    private String miljoe;

    private List<NyEndreInnsatsbehov> nyeEndreInnsatsbehov;

}
