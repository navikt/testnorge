package no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaptArbeidstid {
    private String anvendtRegelKode;

    private Double fastsattArbeidstid;

    private Double naavaerendeArbeidstid;
}
