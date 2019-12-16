package no.nav.dolly.domain.resultset.arenaforvalter;

import java.time.LocalDateTime;

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
public class RsArenaAap115 {

    @ApiModelProperty(
            required = true,
            position = 1,
            dataType = "LocalDateTime"
    )
    private LocalDateTime fraDato;
}