package no.nav.dolly.domain.resultset.arenaforvalter;

import java.time.LocalDateTime;
import java.util.List;

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
public class Arenadata {

    @ApiModelProperty(
            position = 1,
            required = true
    )
    private ArenaBrukertype arenaBrukertype;

    @ApiModelProperty(
            position = 2,
            required = true
    )
    private ArenaKvalifiseringsgruppe kvalifiseringsgruppe;

    @ApiModelProperty(
            position = 3,
            dataType = "LocalDateTime"
    )
    private LocalDateTime inaktiveringDato;

    @ApiModelProperty(
            position = 4,
            value = "Om rettigheten aap115 skal aktiveres på personen"
    )
    private List<RsArenaAap115> aap115;

    @ApiModelProperty(
            position = 5,
            value = "Om rettigheten aap skal aktiveres på personen"
    )
    private List<RsArenaAap> aap;
}