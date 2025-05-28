package no.nav.dolly.domain.resultset.entity.team;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsTeamBruker {

    private Long id;
    private RsBruker bruker;
    private LocalDateTime opprettet;

    @JsonBackReference
    private RsTeam team;

}