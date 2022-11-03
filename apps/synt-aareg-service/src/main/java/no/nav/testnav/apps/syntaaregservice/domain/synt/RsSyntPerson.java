package no.nav.testnav.apps.syntaaregservice.domain.synt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RsSyntPerson {

    private String ident;
    private String identtype;
}
