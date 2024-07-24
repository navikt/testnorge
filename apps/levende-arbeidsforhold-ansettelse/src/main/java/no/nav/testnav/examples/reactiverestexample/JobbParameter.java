package no.nav.testnav.examples.reactiverestexample;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "jobb_parameter")
public class JobbParameter {
    @Id
    @Size(max = 255)
    @Column(name = "navn", nullable = false)
    private String navn;

    @Size(max = 255)
    @NotNull
    @Column(name = "tekst", nullable = false)
    private String tekst;

    @Size(max = 255)
    @Column(name = "verdi")
    private String verdi;

}