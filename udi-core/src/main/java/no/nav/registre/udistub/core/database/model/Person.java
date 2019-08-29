package no.nav.registre.udistub.core.database.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.udistub.core.database.model.opphold.OppholdStatus;
import no.udi.mt_1067_nav_data.v1.JaNeiUavklart;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
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

    private PersonNavn navn;

    private LocalDate foedselsDato;

    @NotNull(message = "fnr must not be null")
    @Column(unique = true)
    private String ident;

    //  FetchType Eager and @Fetch annotation needed to
    //  prevent hibernate from closing the connection prematurely
    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            mappedBy = "person")
    @Fetch(FetchMode.SELECT)
    @JsonManagedReference
    private List<Avgjorelse> avgjoerelser;

    //  FetchType Eager and @Fetch annotation needed to
    //  prevent hibernate from closing the connection prematurely
    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            mappedBy = "person")
    @Fetch(FetchMode.SELECT)
    @JsonManagedReference
    private List<Alias> aliaser;

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
    private Boolean avgjoerelseUavklart;
    private Boolean harOppholdsTillatelse;
    private Boolean flyktning;

    private JaNeiUavklart soeknadOmBeskyttelseUnderBehandling;
    private LocalDate soknadDato;
}
