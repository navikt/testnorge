package no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsAvsendersystem {

    private static final String DEFAULT_SYSTEMNAVN = "ORKESTRATOREN";
    private static final String DEFAULT_SYSTEMVERSJON = "1";

    @JsonProperty
    private String systemnavn;
    @JsonProperty
    private String systemversjon;
    @JsonProperty
    private LocalDateTime innsendingstidspunkt;


    public String getSystemnavn() {
        return Objects.requireNonNullElse(systemnavn, DEFAULT_SYSTEMNAVN);
    }

    public String getSystemversjon() {
        return Objects.requireNonNullElse(systemversjon, DEFAULT_SYSTEMVERSJON);
    }

    public LocalDateTime getInnsendingstidspunkt() {
        return Objects.requireNonNullElse(innsendingstidspunkt, LocalDateTime.now());
    }

}
