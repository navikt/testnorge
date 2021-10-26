package no.nav.dolly.domain.resultset.inntektstub;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class InntektMultiplierWrapper {

    private List<RsInntektsinformasjon> inntektsinformasjon;

    public List<RsInntektsinformasjon> getInntektsinformasjon() {
        if (isNull(inntektsinformasjon)) {
            inntektsinformasjon = new ArrayList<>();
        }
        return inntektsinformasjon;
    }
}
