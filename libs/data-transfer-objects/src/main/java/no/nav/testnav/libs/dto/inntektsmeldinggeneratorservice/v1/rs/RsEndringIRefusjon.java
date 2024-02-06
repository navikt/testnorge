package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RsEndringIRefusjon {

    @JsonProperty
    private LocalDate endringsdato;
    @JsonProperty
    private Double refusjonsbeloepPrMnd;
}
