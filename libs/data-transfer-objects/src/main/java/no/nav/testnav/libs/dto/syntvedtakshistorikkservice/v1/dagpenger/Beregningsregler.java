package no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Beregningsregler {
    @JsonProperty
    private String oppfyllerKravTilFangstOgFiske;

    @JsonProperty
    private String harAvtjentVernepliktSiste3Av12Mnd;
}
