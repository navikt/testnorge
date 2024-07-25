package no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobbParameterVerdier {

    private JobbParameter jobbParameter;
    private List<String> verdier;

    @Override
    public String toString(){
        return "JobbParameter: " +jobbParameter.toString()+ " verdier: " +verdier.toString();
    }
}
