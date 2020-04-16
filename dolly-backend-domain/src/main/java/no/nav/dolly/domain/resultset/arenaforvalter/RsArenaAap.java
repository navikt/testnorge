package no.nav.dolly.domain.resultset.arenaforvalter;

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
public class RsArenaAap {

    @ApiModelProperty(
            position = 1,
            required = true,
            dataType = "LocalDateTime"
    )
    private LocalDateTime fraDato;

    @ApiModelProperty(
            position = 2,
            dataType = "LocalDateTime"
    )
    private LocalDateTime tilDato;
}