package no.nav.dolly.domain.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Table(name = "SOEK")
public class Soek implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SOEK_VERDI")
    private String soekVerdi;

    @CreationTimestamp
    @Column(name = "OPPRETTET")
    private LocalDateTime opprettet;

    @ManyToOne
    @JoinColumn(name = "BRUKER_ID")
    private Bruker bruker;

    @Column(name = "SOEK_TYPE")
    @Enumerated(EnumType.STRING)
    private SoekType soekType;

    public enum SoekType {
        DOLLY,
        TENOR
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Soek soek = (Soek) o;
        return Objects.equals(id, soek.id) && Objects.equals(soekVerdi, soek.soekVerdi) && Objects.equals(opprettet, soek.opprettet) && Objects.equals(bruker, soek.bruker) && soekType == soek.soekType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, soekVerdi, opprettet, bruker, soekType);
    }

    @Override
    public String toString() {
        return "Soek{" +
                "id=" + id +
                ", soekVerdi='" + soekVerdi + '\'' +
                ", opprettet=" + opprettet +
                ", bruker=" + bruker +
                ", soekType=" + soekType +
                '}';
    }
}