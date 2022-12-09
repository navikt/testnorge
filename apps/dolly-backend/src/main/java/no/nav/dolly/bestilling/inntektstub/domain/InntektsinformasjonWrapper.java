package no.nav.dolly.bestilling.inntektstub.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
public class InntektsinformasjonWrapper {

    private List<Inntektsinformasjon> inntektsinformasjon;

    public List<Inntektsinformasjon> getInntektsinformasjon() {
        if (isNull(inntektsinformasjon)) {
            inntektsinformasjon = new ArrayList<>();
        }
        return inntektsinformasjon;
    }
}
