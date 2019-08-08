package no.nav.registre.core.database.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.core.database.model.opphold.OppholdStatus;
import no.udi.mt_1067_nav_data.v1.JaNeiUavklart;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "person")
public class Person {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull(message = "fnr must not be null")
    @Column(unique = true)
    private String fnr;

    @Embedded
    private PersonNavn navn;

    @Embedded
    private MangelfullDato foedselsDato;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "person")
    @JsonManagedReference
    private List<Alias> aliaser;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "person")
    @JsonManagedReference
    private List<Avgjoerelse> avgjoerelser;

    @OneToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "person"
    )
    @JsonManagedReference
    private Arbeidsadgang arbeidsadgang;

    @OneToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "person"
    )
    @JsonManagedReference
    private OppholdStatus oppholdStatus;

    private boolean avgjoerelseUavklart;

    private Boolean oppholdsTilatelse;
    private Boolean flyktning;

    private JaNeiUavklart soeknadOmBeskyttelseUnderBehandling;
    private Date soknadDato;
}
