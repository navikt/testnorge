package no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Barn {
    private List<KodeVerdi> barn;
}
