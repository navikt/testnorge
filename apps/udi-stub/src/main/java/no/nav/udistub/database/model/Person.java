package no.nav.udistub.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.udistub.database.model.opphold.OppholdStatus;
import no.udi.mt_1067_nav_data.v1.JaNeiUavklart;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "person")
@Builder
public class Person {

    @Id
    @GeneratedValue
    private Long id;

    private PersonNavn navn;

    private LocalDate foedselsDato;

    @NotNull(message = "fnr must not be null")
    @Column(unique = true)
    private String ident;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "person")
    private List<Avgjorelse> avgjoerelser;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "person")
    private List<Alias> aliaser;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "person")
    private Arbeidsadgang arbeidsadgang;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "person")
    private ArbeidsadgangUtvidet arbeidsadgangUtvidet;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "person")
    private OppholdStatus oppholdStatus;

    private Boolean avgjoerelseUavklart;
    private Boolean harOppholdsTillatelse;
    private Boolean flyktning;

    private JaNeiUavklart soeknadOmBeskyttelseUnderBehandling;
    private LocalDate soknadDato;

    public List<Avgjorelse> getAvgjoerelser() {
        if (isNull(avgjoerelser)) {
            avgjoerelser = new ArrayList<>();
        }
        return avgjoerelser;
    }

    public List<Alias> getAliaser() {
        if (isNull(aliaser)) {
            aliaser = new ArrayList<>();
        }
        return aliaser;
    }
}
