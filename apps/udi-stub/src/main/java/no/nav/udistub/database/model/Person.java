package no.nav.udistub.database.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.udistub.database.model.opphold.OppholdStatus;
import no.udi.mt_1067_nav_data.v1.JaNeiUavklart;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private PersonNavn navn;

    private LocalDate foedselsDato;

    @NotNull(message = "fnr must not be null")
    @Column(unique = true)
    private String ident;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "person")
    private List<Alias> aliaser;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "person")
    private Arbeidsadgang arbeidsadgang;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "person")
    private OppholdStatus oppholdStatus;

    private Boolean avgjoerelseUavklart;
    private Boolean harOppholdsTillatelse;
    private Boolean flyktning;

    private JaNeiUavklart soeknadOmBeskyttelseUnderBehandling;
    private LocalDate soknadDato;

    public List<Alias> getAliaser() {
        if (isNull(aliaser)) {
            aliaser = new ArrayList<>();
        }
        return aliaser;
    }
}
