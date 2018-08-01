package no.nav.resultSet;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RsBestillingProgress {

    private Long id;

    private Long bestillingsId;

    private String ident;

    private String tpsfSuccessEnv;

    private String sigrunSuccessEnv;

    private String aaregSuccessEnv;

    private String feil;
}
