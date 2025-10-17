package no.nav.dolly.domain.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TEAM_BRUKER")
public class TeamBruker {

    @Id
    private Long id;

    @Column("TEAM_ID")
    private Long teamId;

    @Column("BRUKER_ID")
    private Long brukerId;

    @Column("OPPRETTET")
    private LocalDateTime opprettetTidspunkt;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TeamBruker that = (TeamBruker) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(teamId, that.teamId) &&
                Objects.equals(brukerId, that.brukerId) &&
                Objects.equals(opprettetTidspunkt, that.opprettetTidspunkt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, teamId, brukerId, opprettetTidspunkt);
    }

    @Override
    public String toString() {
        return "TeamBruker{" +
                "id=" + id +
                ", teamId=" + teamId +
                ", brukerId=" + brukerId +
                ", opprettetTidspunkt=" + opprettetTidspunkt +
                '}';
    }
}