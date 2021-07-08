package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RsNaturalytelseDetaljer {

    @JsonProperty
    private String naturaytelseType;
    @JsonProperty
    private LocalDate fom;
    @JsonProperty
    private Double beloepPrMnd;

    public Optional<String> getNaturaytelseType() {
        return Optional.ofNullable(naturaytelseType);
    }

    public Optional<LocalDate> getFom() {
        return Optional.ofNullable(fom);
    }

    public Optional<Double> getBeloepPrMnd() {
        return Optional.ofNullable(beloepPrMnd);
    }
}
