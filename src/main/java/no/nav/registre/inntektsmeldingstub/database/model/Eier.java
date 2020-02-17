package no.nav.registre.inntektsmeldingstub.database.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

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
    @JsonBackReference
    @OneToMany(mappedBy = "eier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inntektsmelding> inntektsmeldinger;


    public List<Inntektsmelding> getInntektsmeldinger() {
        if (isNull(inntektsmeldinger)) {
            inntektsmeldinger = new ArrayList<>();
        }
        return inntektsmeldinger;
    }
}