package no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;

@Entity
@Data
@Builder
//@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "verdier")
public class VerdierEntity implements Serializable {
    /*
    @ManyToOne(fetch = FetchType.LAZY)
    private JobbParameterEntity jobbParameterEntity;


     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @JoinColumn(name = "verdi_navn")
    private String navn;

    @Column(name = "verdi_verdi")
    private String verdi;
    @Override
    public String toString(){
        return navn+", "+verdi ;
    }
}
