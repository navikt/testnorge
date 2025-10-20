package no.nav.testnav.identpool.domain;

import jakarta.validation.constraints.NotNull;
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
@Table(name = "AJOURHOLD")
public class Ajourhold {

    @Id
    @Column("ID")
    private Long identity;

    @NotNull
    @Column("STATUS")
    private BatchStatus status;

    @Column("MELDING")
    private String melding;

    @NotNull
    @Column("SISTOPPDATERT")
    private LocalDateTime sistOppdatert;

    @Column("FEILMELDING")
    private String feilmelding;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ajourhold ajourhold = (Ajourhold) o;
        return Objects.equals(identity, ajourhold.identity) && status == ajourhold.status && Objects.equals(melding, ajourhold.melding) && Objects.equals(sistOppdatert, ajourhold.sistOppdatert) && Objects.equals(feilmelding, ajourhold.feilmelding);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identity, status, melding, sistOppdatert, feilmelding);
    }

    @Override
    public String toString() {
        return "Ajourhold{" +
                "identity=" + identity +
                ", status=" + status +
                ", melding='" + melding + '\'' +
                ", sistOppdatert=" + sistOppdatert +
                ", feilmelding='" + feilmelding + '\'' +
                '}';
    }
}
