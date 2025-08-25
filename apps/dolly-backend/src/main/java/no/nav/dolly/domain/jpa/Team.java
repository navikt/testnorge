package no.nav.dolly.domain.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TEAM")
public class Team implements Serializable {

    @Id
    private Long id;

    @Column("BRUKER_ID")
    private Long brukerId;

    @Column("NAVN")
    private String navn;

    @Column("OPPRETTET")
    private LocalDateTime opprettetTidspunkt;

    @Column("OPPRETTET_AV")
    private Long opprettetAvId;

    @Transient
    private Bruker opprettetAv;

    @Column("BESKRIVELSE")
    private String beskrivelse;

    @Transient
    @Builder.Default
    private Set<Bruker> brukere = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(id, team.id) &&
                Objects.equals(brukerId, team.brukerId) &&
                Objects.equals(navn, team.navn) &&
                Objects.equals(opprettetTidspunkt, team.opprettetTidspunkt) &&
                Objects.equals(opprettetAvId, team.opprettetAvId) &&
                Objects.equals(opprettetAv, team.opprettetAv) &&
                Objects.equals(beskrivelse, team.beskrivelse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, brukerId, navn,
                opprettetTidspunkt, opprettetAvId,
                opprettetAv, beskrivelse);
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", brukerId=" + brukerId +
                ", navn='" + navn + '\'' +
                ", opprettetTidspunkt=" + opprettetTidspunkt +
                ", opprettetAvId=" + opprettetAvId +
                ", opprettetAv=" + opprettetAv +
                ", beskrivelse='" + beskrivelse + '\'' +
                '}';
    }
}