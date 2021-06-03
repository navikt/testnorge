package no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Objects;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class RsAvsendersystem {
    private static final String DEFAULT_SYSTEMNAVN = "ORKESTRATOREN";
    private static final String DEFAULT_SYSTEMVERSJON = "1";
    private static final String DEFAULT_INNSENDINGSTIDSPUNKT = "9999-12-31T12:00:00";

    @JsonProperty(defaultValue = DEFAULT_SYSTEMNAVN)
    private String systemnavn;

    @JsonProperty(defaultValue = DEFAULT_SYSTEMVERSJON)
    private String systemversjon;

    @JsonProperty(defaultValue = DEFAULT_INNSENDINGSTIDSPUNKT)
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
