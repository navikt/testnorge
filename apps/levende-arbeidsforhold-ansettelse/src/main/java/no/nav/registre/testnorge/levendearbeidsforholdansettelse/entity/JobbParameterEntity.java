package no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    @Column(name = "PARAM_NAVN")
    private String param_navn;
    @Id
    @Column(name = "PARAM_TEKST")
    private String param_tekst;
    @Id
    @Column(name = "PARAM_VERDI")
    private String param_verdi;

    // Getters and Setters
    public String hentParamNavn(){
        return param_navn;
    }
}
