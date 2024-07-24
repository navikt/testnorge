package no.nav.testnav.examples.reactiverestexample;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "verdier")
public class Verdier {
    @Id
    @ColumnDefault("nextval('verdier_id_seq'::regclass)")
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verdi_navn")
    private JobbParameter verdiNavn;

    @Size(max = 255)
    @Column(name = "verdi_verdi")
    private String verdiVerdi;

}