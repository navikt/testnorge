package no.nav.dolly.bestilling.pdlforvalter.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.tpsf.adresse.IdentHistorikk;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlOpprettPerson {

    private String opprettetIdent;
    private List<IdentHistorikk> opprettetIdentHistorikk;
}
