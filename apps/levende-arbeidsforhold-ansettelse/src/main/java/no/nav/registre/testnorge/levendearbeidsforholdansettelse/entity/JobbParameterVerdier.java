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
    private String navn;
    private String tekst;
    private String verdi;
    private List<String> verdier;
}
