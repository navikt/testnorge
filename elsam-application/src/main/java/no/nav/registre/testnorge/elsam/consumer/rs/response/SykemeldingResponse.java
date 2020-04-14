package no.nav.registre.testnorge.elsam.consumer.rs.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SykemeldingResponse {

    private String ident;
    private List<String> sykemeldinger;
    private String historie;
}
