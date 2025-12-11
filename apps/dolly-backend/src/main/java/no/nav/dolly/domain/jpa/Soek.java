package no.nav.dolly.domain.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "soek")
public class Soek implements Serializable {

    @Id
    private Long id;

    @Column("soek_verdi")
    private String soekVerdi;

    @Column("opprettet")
    private LocalDateTime opprettetTidspunkt;

    @Column("bruker_id")
    private Long brukerId;

    @Column("soek_type")
    private SoekType soekType;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Soek soek = (Soek) o;
        return Objects.equals(id, soek.id) && Objects.equals(soekVerdi, soek.soekVerdi) && Objects.equals(opprettetTidspunkt, soek.opprettetTidspunkt) && Objects.equals(brukerId, soek.brukerId) && soekType == soek.soekType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, soekVerdi, opprettetTidspunkt, brukerId, soekType);
    }

    @Override
    public String toString() {
        return "Soek{" +
                "id=" + id +
                ", soekVerdi='" + soekVerdi + '\'' +
                ", opprettetTidspunkt=" + opprettetTidspunkt +
                ", brukerId=" + brukerId +
                ", soekType=" + soekType +
                '}';
    }

    public enum SoekType {
        DOLLY,
        TENOR
    }
}