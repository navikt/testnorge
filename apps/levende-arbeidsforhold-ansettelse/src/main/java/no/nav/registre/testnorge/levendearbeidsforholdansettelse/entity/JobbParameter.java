package no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "verdiNavn", cascade = CascadeType.ALL)
    private List<Verdier> verdierList;

    @Override
    public String toString(){
        return "Navn: " + navn + ", tekst: " + tekst + " verdi: " +verdi;
    }

}