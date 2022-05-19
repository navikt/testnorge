package no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class TaptArbeidstid {
    @JsonProperty
    private String anvendtRegelKode;

    @JsonProperty
    private Double fastansattArbeidstid;

    @JsonProperty
    private Double naavaerendeArbeidstid;
}
