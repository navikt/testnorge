package no.nav.dolly.bestilling.inntektstub.domain;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InntektsinformasjonWrapper {

    private List<Inntektsinformasjon> inntektsinformasjon;

    public List<Inntektsinformasjon> getInntektsinformasjon() {
        if (isNull(inntektsinformasjon)) {
            inntektsinformasjon = new ArrayList<>();
        }
        return inntektsinformasjon;
    }
}
