package no.nav.registre.aareg.syntetisering;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
