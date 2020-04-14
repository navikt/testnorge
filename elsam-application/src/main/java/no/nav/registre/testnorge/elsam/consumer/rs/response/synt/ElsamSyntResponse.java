package no.nav.registre.testnorge.elsam.consumer.rs.response.synt;

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
public class ElsamSyntResponse {

    private String endringshistorikk;
    private List<SyntSykmeldingResponse> sykmeldinger;
}
