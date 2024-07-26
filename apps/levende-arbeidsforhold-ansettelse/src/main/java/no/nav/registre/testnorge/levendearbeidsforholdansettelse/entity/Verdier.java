package no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;

@Getter
@Setter
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
 property = "verdiNavn")
@Table(name = "verdier")
public class Verdier implements Serializable {
    @Id
    @ColumnDefault("nextval('verdier_id_seq'::regclass)")
    @Column(name = "id", nullable = false)
    private Integer id;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verdi_navn")
    private JobbParameter verdiNavn;

    @Size(max = 255)
    @Column(name = "verdi_verdi")
    private String verdiVerdi;

    @Override
    public String toString(){
        //String re = "verdiNavn: {}, verdiVerdi: {}", verdiNavn.verdiNavn, verdiVerdi
        return "verdiNavn: "+ verdiNavn.toString()+ "verdiVerdi: " +  verdiVerdi;
    }

}