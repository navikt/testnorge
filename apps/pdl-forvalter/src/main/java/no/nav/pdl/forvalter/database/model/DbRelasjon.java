package no.nav.pdl.forvalter.database.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@Table(name = "relasjon")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DbRelasjon {

    private static final String SEQUENCE_STYLE_GENERATOR = "org.hibernate.id.enhanced.SequenceStyleGenerator";

    @Id
    @GeneratedValue(generator = "relasjonIdGenerator")
    @GenericGenerator(name = "relasjonIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "relasjon_sequence"),
            @Parameter(name = "increment_size", value = "1"),
    })
    private Long id;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DbRelasjon that = (DbRelasjon) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}