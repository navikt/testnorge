package no.nav.dolly.domain.resultset.arenaforvalter;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @JsonIgnore
    private String personident;

    private ArenaBrukertype arenaBrukertype;
    private ArenaKvalifiseringsgruppe kvalifiseringsgruppe;
    private LocalDateTime inaktiveringDato;
}