package no.nav.dolly.domain.resultset;

import java.util.List;

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

    private List<String> tpsfSuccessEnv;

    private String sigrunstubStatus;

    private String krrstubStatus;

    private String feil;
}
