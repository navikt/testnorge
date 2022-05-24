package no.nav.dolly.bestilling.udistub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsAliasRequest {

    public enum IdentType {DNR, FNR}

    private List<String> environments;
    private String ident;
    private List<AliasSpesification> aliaser;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AliasSpesification {

        private Boolean nyIdent;
        private IdentType identtype;
    }
}
