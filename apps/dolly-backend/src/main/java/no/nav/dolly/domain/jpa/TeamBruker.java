package no.nav.dolly.domain.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TEAM_BRUKER")
public class TeamBruker {

    @EmbeddedId
    private TeamBrukerId id;

    @CreationTimestamp
    private LocalDateTime opprettet;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TeamBruker that = (TeamBruker) o;
        return Objects.equals(id, that.id) && Objects.equals(opprettet, that.opprettet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, opprettet);
    }

    @Override
    public String toString() {
        return "TeamBruker{" +
                "id=" + id +
                ", opprettet=" + opprettet +
                '}';
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class TeamBrukerId implements Serializable {

        @Column(name = "TEAM_ID")
        private Long teamId;

        @Column(name = "BRUKER_ID")
        private Long brukerId;

    }
}