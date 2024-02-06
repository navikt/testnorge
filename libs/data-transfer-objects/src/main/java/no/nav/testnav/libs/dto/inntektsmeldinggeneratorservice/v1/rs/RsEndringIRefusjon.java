package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RsEndringIRefusjon {

    @JsonProperty
    private LocalDate endringsdato;
    @JsonProperty
    private Double refusjonsbeloepPrMnd;

    public Optional<LocalDate> getEndringsdato() {
        return Optional.ofNullable(endringsdato);
    }

    public Optional<Double> getRefusjonsbeloepPrMnd() {
        return Optional.ofNullable(refusjonsbeloepPrMnd);
    }
}
