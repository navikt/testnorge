package no.nav.registre.testnorge.identservice.testdata.response;

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
public class IdentMedStatus {

    private String ident;
    private String status;
    private String statusCode;
}
