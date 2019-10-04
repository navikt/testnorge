package no.nav.dolly.domain.resultset.entity.team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsTeamMedIdOgNavn {
    private Long id;
    private String navn;

}
