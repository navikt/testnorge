package no.nav.dolly.domain.resultset.tpsf;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsSivilstandRequest {

    @ApiModelProperty(
        position = 1,
        required = true,
        value = "Sivilstand i hht kodeverk 'Sivilstander'"
    )
    private String sivilstand;

    @ApiModelProperty(
            position = 2,
            required = true,
            dataType = "LocalDateTime",
            value = "Sivilstand gjelder fra denne dato"
    )
    private LocalDateTime sivilstandRegdato;
}
