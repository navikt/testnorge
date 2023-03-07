package no.nav.pdl.forvalter.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.nav.pdl.forvalter.database.JSONUserType;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@ToString
@Entity
@Table(name = "person")
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DbPerson {

    private static final String SEQUENCE_STYLE_GENERATOR = "org.hibernate.id.enhanced.SequenceStyleGenerator";

    @Id
    @GeneratedValue(generator = "personIdGenerator")
    @GenericGenerator(name = "personIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "person_sequence"),
            @Parameter(name = "increment_size", value = "1"),
    })
    private Long id;

    @Column(name = "sist_oppdatert")
    private LocalDateTime sistOppdatert;
    private String ident;

    private String fornavn;
    private String mellomnavn;
    private String etternavn;


    @Type(JSONUserType.class)
    private PersonDTO person;

    @OrderBy("relasjonType desc, id desc")
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "person", cascade = CascadeType.ALL)
    private List<DbRelasjon> relasjoner;
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "person", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<DbAlias> alias;

    public List<DbRelasjon> getRelasjoner() {
        if (isNull(relasjoner)) {
            relasjoner = new ArrayList<>();
        }
        return relasjoner;
    }

    public List<DbAlias> getAlias() {
        if (isNull(alias)) {
            alias = new ArrayList<>();
        }
        return alias;
    }
}