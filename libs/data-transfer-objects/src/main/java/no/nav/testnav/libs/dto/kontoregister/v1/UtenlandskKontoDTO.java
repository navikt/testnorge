package no.nav.testnav.libs.dto.kontoregister.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UtenlandskKontoDTO {
    private String banknavn;
    private String bankkode;
    private String bankLandkode;
    private String valutakode;
    private String swiftBicKode;
    private String bankadresse1;
    private String bankadresse2;
    private String bankadresse3;
}
