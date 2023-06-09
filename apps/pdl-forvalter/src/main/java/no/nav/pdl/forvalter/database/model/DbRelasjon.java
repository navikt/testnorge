package no.nav.pdl.forvalter.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

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

    @JsonIgnore
    public boolean isForeldreansvar() {

        return relasjonType == RelasjonType.FORELDREANSVAR_FORELDER ||
                relasjonType == RelasjonType.FORELDREANSVAR_BARN;
    }

    @JsonIgnore
    public boolean isVergemaal() {

        return relasjonType == RelasjonType.VERGE ||
                relasjonType == RelasjonType.VERGE_MOTTAKER;
    }
}