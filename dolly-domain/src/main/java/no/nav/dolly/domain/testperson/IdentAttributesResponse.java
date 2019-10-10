package no.nav.dolly.domain.testperson;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IdentAttributesResponse {

    private String ident;
    private boolean ibruk;
    private String beskrivelse;
    private Long gruppeId;
}
