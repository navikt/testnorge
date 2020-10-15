package no.nav.dolly.domain.resultset.arenaforvalter;

import java.time.LocalDateTime;
import java.util.List;
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
public class Arenadata {

    @Schema(required = true)
    private ArenaBrukertype arenaBrukertype;

    @Schema(required = true)
    private ArenaKvalifiseringsgruppe kvalifiseringsgruppe;

    @Schema(type = "LocalDateTime")
    private LocalDateTime inaktiveringDato;

    @Schema(description = "Om rettigheten aap115 skal aktiveres på personen")
    private List<RsArenaAap115> aap115;

    @Schema(description = "Om rettigheten aap skal aktiveres på personen")
    private List<RsArenaAap> aap;
}