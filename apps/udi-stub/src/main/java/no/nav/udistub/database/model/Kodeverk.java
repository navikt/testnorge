package no.nav.udistub.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "kodeverk")
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class Kodeverk {

    @Id
    @GeneratedValue
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
