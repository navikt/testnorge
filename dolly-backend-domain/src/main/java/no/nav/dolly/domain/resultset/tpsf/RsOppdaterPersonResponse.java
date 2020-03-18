package no.nav.dolly.domain.resultset.tpsf;

import java.util.List;

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
public class RsOppdaterPersonResponse {

    private List<IdentTuple> identTupler;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IdentTuple {

        private String ident;
        private boolean lagtTil;
    }
}

