package no.nav.pdl.forvalter.database.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "relasjon")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DbRelasjon {

    private static final String SEQUENCE_STYLE_GENERATOR = "org.hibernate.id.enhanced.SequenceStyleGenerator";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "relasjonIdGenerator")
    @SequenceGenerator(name = "relasjonIdGenerator", sequenceName = "relasjon_sequence", allocationSize = 1)
    private Long id;

    @UpdateTimestamp
    @Column(name = "sist_oppdatert")
    private LocalDateTime sistOppdatert;

    @Enumerated(EnumType.STRING)
    private RelasjonType relasjonType;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false, updatable = false)
    private DbPerson person;

    @ManyToOne
    @JoinColumn(name = "relatert_person_id", nullable = false, updatable = false)
    private DbPerson relatertPerson;

    @Version
    private Integer versjon;

    public boolean hasGammelIdentitet() {
        return relasjonType == RelasjonType.GAMMEL_IDENTITET;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbRelasjon that = (DbRelasjon) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getSistOppdatert() != null ? !getSistOppdatert().equals(that.getSistOppdatert()) : that.getSistOppdatert() != null)
            return false;
        if (getRelasjonType() != that.getRelasjonType()) return false;
        if (getPerson() != null ? !getPerson().equals(that.getPerson()) : that.getPerson() != null) return false;
        return getRelatertPerson() != null ? getRelatertPerson().equals(that.getRelatertPerson()) : that.getRelatertPerson() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getSistOppdatert() != null ? getSistOppdatert().hashCode() : 0);
        result = 31 * result + (getRelasjonType() != null ? getRelasjonType().hashCode() : 0);
        result = 31 * result + (getPerson() != null ? getPerson().hashCode() : 0);
        result = 31 * result + (getRelatertPerson() != null ? getRelatertPerson().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DbRelasjon{" +
                "id=" + id +
                ", sistOppdatert=" + sistOppdatert +
                ", relasjonType=" + relasjonType +
                ", person=" + person +
                ", relatertPerson=" + relatertPerson +
                '}';
    }
}