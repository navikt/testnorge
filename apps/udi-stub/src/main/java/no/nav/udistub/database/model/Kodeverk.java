package no.nav.udistub.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "kodeverk")
public class Kodeverk {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_generator")
    @SequenceGenerator(name = "sequence_generator", sequenceName = "hibernate_sequence", allocationSize = 1)
    @JsonIgnore
    private Long id;

    @Column(unique = true)
    private String kode;
    private String visningsnavn;
    private String type;

    private LocalDate aktivTom;
    private LocalDate aktivFom;

    public no.udi.common.v2.Kodeverk udiKodeverk() {
        no.udi.common.v2.Kodeverk kodeverk = new no.udi.common.v2.Kodeverk();
        kodeverk.setKode(this.kode);
        kodeverk.setType(this.type);
        kodeverk.setVisningsnavn(this.visningsnavn);
        return kodeverk;
    }

}
