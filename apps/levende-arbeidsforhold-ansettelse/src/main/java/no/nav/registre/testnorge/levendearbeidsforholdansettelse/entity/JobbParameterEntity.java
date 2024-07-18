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

    @Column(name = "PARAM_NAVN")
    private String param_navn;

    @Column(name = "PARAM_TEKST")
    private String param_tekst;

    @Column(name = "PARAM_VERDI")
    private String param_verdi;

    // Getters and Setters
    public String hentParamNavn(){
        return param_navn;
    }
}
