package no.nav.registre.testnorge.elsam.consumer.rs.response.aareg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AaregResponse {

    private String fnr;
    private List<Arbeidsforhold> arbeidsforhold;
}
