package no.nav.dolly.domain.resultset.tpsf;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdentStatus {

    private String ident;
    private String status;
    private boolean available;
}