package no.nav.dolly.domain.resultset.tpsf;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsTpsfBasisMedSivilstandBestilling extends RsTpsfBasisBestilling {

    @ApiModelProperty(
            position = 26,
            value = "Sivilstand i hht kodeverk 'Sivilstander'"
    )
    private String sivilstand;

    @ApiModelProperty(
            position = 27,
            dataType = "LocalDateTime",
            value = "Dato sivilstand. Hvis blankt settes dagens dato"
    )
    private LocalDateTime sivilstandRegdato;
}
