package no.nav.registre.testnorge.arena.consumer.rs.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.NyEndreInnsatsbehov;


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
