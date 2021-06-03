package no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Builder
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class RsRefusjon {

    @JsonProperty
    private Double refusjonsbeloepPrMnd;

    @JsonProperty
    private LocalDate refusjonsopphoersdato;

    @JsonProperty
    private List<RsEndringIRefusjon> endringIRefusjonListe;

    public List<RsEndringIRefusjon> getEndringIRefusjonListe() {
        return Objects.requireNonNullElse(endringIRefusjonListe, Collections.emptyList());
    }
}
