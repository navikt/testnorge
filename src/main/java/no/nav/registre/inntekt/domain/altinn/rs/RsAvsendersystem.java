package no.nav.registre.inntekt.domain.altinn.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@ApiModel
@Value
@Builder
@AllArgsConstructor
public class RsAvsendersystem {
    private static final String SYSTEMNAVN = "ORKESTRATOREN";
    private static final String SYSTEMVERSJON = "0.0.0";

    @JsonProperty(defaultValue = SYSTEMNAVN)
    @ApiModelProperty
    private String systemnavn;
    @JsonProperty(defaultValue = SYSTEMVERSJON)
    @ApiModelProperty
    private String systemversjon;
    @JsonProperty(defaultValue = "Systemtiden når request blir gitt")
    @ApiModelProperty(example = "yyyy-MM-ddThh:mm:ss")
    private LocalDateTime innsendingstidspunkt;

    @SuppressWarnings("unused")
    public RsAvsendersystem() {
        this.systemnavn = SYSTEMNAVN;
        this.systemversjon = SYSTEMVERSJON;
        this.innsendingstidspunkt = LocalDateTime.now();
    }
}
