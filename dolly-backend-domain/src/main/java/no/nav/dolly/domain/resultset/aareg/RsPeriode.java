package no.nav.dolly.domain.resultset.aareg;

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
public class RsPeriode {

    @ApiModelProperty(
            value = "Dato fra-og-med",
            dataType = "LocalDateTime",
            required = true,
            position = 1
    )
    private LocalDateTime fom;

    @ApiModelProperty(
            value = "Dato til-og-med",
            dataType = "LocalDateTime",
            position = 2
    )
    private LocalDateTime tom;
}
