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
import jakarta.persistence.SequenceGenerator;
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

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "person")
@Builder
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_generator")
    @SequenceGenerator(name = "sequence_generator", sequenceName = "hibernate_sequence", allocationSize = 1)
    private Long id;

    private PersonNavn navn;

    private LocalDate foedselsDato;

    @NotNull(message = "fnr must not be null")
    @Column(unique = true)
    private String ident;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "person")
    @Builder.Default
    private List<Alias> aliaser = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "person")
    private Arbeidsadgang arbeidsadgang;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "person")
    private OppholdStatus oppholdStatus;

    private Boolean avgjoerelseUavklart;
    private Boolean harOppholdsTillatelse;
    private Boolean flyktning;

    private JaNeiUavklart soeknadOmBeskyttelseUnderBehandling;
    private LocalDate soknadDato;
}
