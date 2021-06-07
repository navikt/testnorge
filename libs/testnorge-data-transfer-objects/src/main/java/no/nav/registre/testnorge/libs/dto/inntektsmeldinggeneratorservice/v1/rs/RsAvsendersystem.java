package no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsAvsendersystem {

    @JsonProperty
    private String systemnavn;
    @JsonProperty
    private String systemversjon;
    @JsonProperty
    private LocalDateTime innsendingstidspunkt;

    public LocalDateTime getInnsendingstidspunkt() {
        return innsendingstidspunkt;
    }
}
