package no.nav.registre.inntektsmeldingstub.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@Table(name = "eier")
@AllArgsConstructor
@NoArgsConstructor
public class Eier {

    @Id
    @GeneratedValue
    private Long id;
    private String navn;
    @OneToMany(mappedBy = "eier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inntektsmelding> inntektsmeldinger = Collections.emptyList();

    public void addInntektsmelding(Inntektsmelding melding) {
        melding.setEier(this);
        inntektsmeldinger.add(melding);
    }
}