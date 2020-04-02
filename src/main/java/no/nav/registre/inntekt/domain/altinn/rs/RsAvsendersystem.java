package no.nav.registre.inntekt.domain.altinn.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@NoArgsConstructor(force = true)
@Builder
@AllArgsConstructor
public class RsAvsendersystem {
    @JsonProperty
    @ApiModelProperty(required = true)
    private String systemnavn;
    @JsonProperty
    @ApiModelProperty(required = true)
    private String systemversjon;
    @JsonProperty
    @ApiModelProperty(example = "yyyy-MM-ddThh:mm:ss")
    private LocalDateTime innsendingstidspunkt;

    public RsAvsendersystem(String systemnavn, String systemversjon) {
        this.systemnavn = systemnavn;
        this.systemversjon = systemversjon;
        this.innsendingstidspunkt = LocalDateTime.now();
    }
}
