package no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RsRefusjon {

    @JsonProperty
    private Double refusjonsbeloepPrMnd;
    @JsonProperty
    private LocalDate refusjonsopphoersdato;
    @JsonProperty
    private List<RsEndringIRefusjon> endringIRefusjonListe;

    public Optional<Double> getRefusjonsbeloepPrMnd() {
        return Optional.ofNullable(refusjonsbeloepPrMnd);
    }

    public Optional<LocalDate> getRefusjonsopphoersdato() {
        return Optional.ofNullable(refusjonsopphoersdato);
    }

    public Optional<List<RsEndringIRefusjon>> getEndringIRefusjonListe() {
        return Optional.ofNullable(endringIRefusjonListe);
    }
}
