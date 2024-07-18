package no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@Builder
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "JOBB_PARAMETER")
public class JobbParameterEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int ID;

    @Column(name = "NAVN")
    private String navn;

    @Column(name = "TEKST")
    private String tekst;

    @Column(name = "VERDI")
    private String verdi;

    // Getters and Setters
    public String hentParamNavn(){
        return navn;
    }
}
