package no.nav.registre.testnorge.elsam.consumer.rs.response.aareg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Arbeidsforhold {

    private AnsettelsesPeriode ansettelsesPeriode;
    private Arbeidsavtale arbeidsavtale;
    private Arbeidsgiver arbeidsgiver;
}
