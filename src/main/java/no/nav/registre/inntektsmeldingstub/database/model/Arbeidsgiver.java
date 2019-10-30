package no.nav.registre.inntektsmeldingstub.database.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "arbeidsgiver")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Arbeidsgiver {

    @JsonManagedReference("inntektsmeldinger")
    @OneToMany(mappedBy = "arbeidsgiver", cascade = CascadeType.ALL)
    List<Inntektsmelding> inntektsmeldinger;
    @Id
    @GeneratedValue
    private Integer id;
    @Column(unique = true)
    private String virksomhetsnummer;
    private String kontaktinformasjonNavn;
    private String telefonnummer;

}
