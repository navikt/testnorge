package no.nav.dolly.domain.resultset.entity.team;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsTeamUpdate {

    private String navn;
    private String beskrivelse;

    @Builder.Default
    private Set<String> brukere = new HashSet<>();
}