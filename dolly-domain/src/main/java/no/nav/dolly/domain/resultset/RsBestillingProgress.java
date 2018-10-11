package no.nav.dolly.domain.resultset;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RsBestillingProgress {

    private Long id;

    private Long bestillingsId;

    private String ident;

    private List<String> tpsfSuccessEnv;

    private List<String> sigrunSuccessEnv;

    private List<String> aaregSuccessEnv;

    private String feil;
}
