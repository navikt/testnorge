package no.nav.dolly.domain.resultset.entity.team;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsTeamWithBrukere {


    private Long id;
    private String brukerId;
    private String navn;
    private String beskrivelse;
    private LocalDateTime opprettet;
    private RsBrukerUtenFavoritter opprettetAv;

    @Builder.Default
    private Set<RsBrukerUtenFavoritter> brukere = new HashSet<>();
}