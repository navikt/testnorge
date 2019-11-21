package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
public class Avsendersystem {

    @JsonProperty
    @ApiModelProperty(required = true)
    private String systemnavn;
    @JsonProperty
    @ApiModelProperty(required = true)
    private String systemversjon;
    @JsonProperty
    @ApiModelProperty(required = true)
    private LocalDateTime innsendingstidspunkt;

}
