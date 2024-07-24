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
@Table(name = "VERDIER")
public class VerdierEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @JoinColumn(name = "VERDI_NAVN")
    private String navn;

    @Column(name = "VERDI_VERDI")
    private String verdi;
    @Override
    public String toString(){
        return navn+", "+verdi ;
    }
}
