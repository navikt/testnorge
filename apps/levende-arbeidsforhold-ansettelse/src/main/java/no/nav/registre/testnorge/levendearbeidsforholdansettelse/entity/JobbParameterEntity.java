package no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Builder
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "jobb_parameter")
public class JobbParameterEntity implements Serializable {
    /*
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int ID;


     */
    @Id
    @Column(name = "navn")
    private String navn;

    @Column(name = "tekst")
    private String tekst;

    @Column(name = "verdi")
    private String verdi;

    @OneToMany//mappedBy = "jobbParameterEntity"
    private Collection<VerdierEntity> verdier = new ArrayList<>();
    // Getters and Setters
    public String toString(){
        return "navn: " + navn + " tekst: " + tekst + " verdi: " +verdi;
    }
}
