package no.nav.dolly.domain.resultset.arenaforvalter;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Arenadata {

    @Schema(required = true)
    private ArenaBrukertype arenaBrukertype;

    @Schema(required = true)
    private ArenaKvalifiseringsgruppe kvalifiseringsgruppe;

    @Schema(description = "Automatisk innsending av meldekort")
    private Boolean automatiskInnsendingAvMeldekort;

    @Schema(type = "LocalDateTime")
    private LocalDateTime inaktiveringDato;

    @Schema(description = "Om rettigheten aap115 skal aktiveres på personen")
    private List<RsArenaAap115> aap115;

    @Schema(description = "Om rettigheten aap skal aktiveres på personen")
    private List<RsArenaAap> aap;

    @Schema(description = "Om rettigheten aap skal aktiveres på personen")
    private List<RsArenaDagpenger> dagpenger;

    public List<RsArenaAap115> getAap115() {
        if (isNull(aap115)) {
            aap115 = new ArrayList<>();
        }
        return aap115;
    }

    public List<RsArenaAap> getAap() {
        if (isNull(aap)) {
            aap = new ArrayList<>();
        }
        return aap;
    }

    public List<RsArenaDagpenger> getDagpenger() {
        if (isNull(dagpenger)) {
            dagpenger = new ArrayList<>();
        }
        return dagpenger;
    }
}