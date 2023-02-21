package no.nav.pdl.forvalter.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Data
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
            @Parameter(name = "sequence_name", value = "relasjon_sequence")
    })
    private Long id;

    private LocalDateTime sistOppdatert;

    @Enumerated(EnumType.STRING)
    private RelasjonType relasjonType;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false, updatable = false)
    private DbPerson person;

    @ManyToOne
    @JoinColumn(name = "relatert_person_id", nullable = false, updatable = false)
    private DbPerson relatertPerson;
}