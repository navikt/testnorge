package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
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
