package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(required = true)
    private String systemnavn;
    @JsonProperty
    @ApiModelProperty(required = true)
    private String systemversjon;
    @JsonProperty
    @ApiModelProperty(example = "yyyy-MM-ddThh:mm:ss")
    private LocalDateTime innsendingstidspunkt;

    public Optional<LocalDateTime> getInnsendingstidspunkt() { return Optional.ofNullable(innsendingstidspunkt); }
}
