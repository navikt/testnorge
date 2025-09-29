package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsAvsendersystem {

    private static final String DEFAULT_SYSTEMNAVN = "ORKESTRATOREN";
    private static final String DEFAULT_SYSTEMVERSJON = "1";

    private String systemnavn;
    private String systemversjon;
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
