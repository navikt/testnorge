package no.nav.registre.medl.consumer.rs.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AktoerResponse {

    private List<AktoerInnhold> identer;
    private String feilmelding;

}
