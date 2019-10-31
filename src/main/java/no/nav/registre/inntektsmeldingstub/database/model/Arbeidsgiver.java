package no.nav.registre.inntektsmeldingstub.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "arbeidsgiver")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Arbeidsgiver {

    @JsonIgnore
    @OneToMany(mappedBy = "arbeidsgiver", cascade = CascadeType.ALL)
    @Builder.Default
    List<Inntektsmelding> inntektsmeldinger = Collections.emptyList();
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    @JsonProperty("ident")
    private String virksomhetsnummer;
    private String kontaktinformasjonNavn;
    private String telefonnummer;

}
