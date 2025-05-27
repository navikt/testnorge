package no.nav.dolly.domain.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TEAM_BRUKER")
@IdClass(TeamBruker.TeamBrukerId.class)
public class TeamBruker {

    @Id
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @Id
    @ManyToOne
    @JoinColumn(name = "bruker_id")
    private Bruker bruker;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamBrukerId implements Serializable {
        private Long team;
        private String bruker;
    }
}