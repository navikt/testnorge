package no.nav.registre.inntekt.domain.altinn.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Objects;

@ApiModel
@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class RsAvsendersystem {
    private static final String DEFAULT_SYSTEMNAVN = "ORKESTRATOREN";
    private static final String DEFAULT_SYSTEMVERSJON = "1";

    @JsonProperty(defaultValue = DEFAULT_SYSTEMNAVN)
    @ApiModelProperty
    private String systemnavn;
    @JsonProperty(defaultValue = DEFAULT_SYSTEMVERSJON)
    @ApiModelProperty
    private String systemversjon;
    @JsonProperty(defaultValue = "Systemtiden når request blir gitt")
    @ApiModelProperty(example = "yyyy-MM-ddThh:mm:ss")
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
