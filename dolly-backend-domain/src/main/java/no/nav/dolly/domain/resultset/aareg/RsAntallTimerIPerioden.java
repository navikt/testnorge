package no.nav.dolly.domain.resultset.aareg;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class RsAntallTimerIPerioden {

    @Schema(required = true)
    private RsPeriode periode;

    @Schema(required = true)
    private BigDecimal antallTimer;
}
