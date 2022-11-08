package no.nav.registre.sdforvalter.consumer.rs.aareg.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsAaregSyntetiseringsRequest {

    private RsSyntetiskArbeidsforhold arbeidsforhold;
    private String arkivreferanse;
    private List<String> environments;

}
