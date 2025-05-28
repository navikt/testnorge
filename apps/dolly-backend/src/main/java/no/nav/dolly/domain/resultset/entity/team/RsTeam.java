package no.nav.dolly.domain.resultset.entity.team;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsTeam {

    private Long id;
    private String navn;
    private String beskrivelse;

    @JsonManagedReference
    private Set<RsTeamBruker> brukere;
}