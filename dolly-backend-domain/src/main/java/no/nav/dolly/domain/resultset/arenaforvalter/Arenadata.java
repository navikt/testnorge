package no.nav.dolly.domain.resultset.arenaforvalter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Arenadata {

    private ArenaBrukertype arenaBrukertype;
    private ArenaKvalifiseringsgruppe kvalifiseringsgruppe;
    private LocalDateTime inaktiveringDato;
    private List<RsArenaAap115> aap115;
    private List<RsArenaAap> aap;
}