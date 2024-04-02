package no.nav.testnav.libs.dto.endringsmelding.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoedselsmeldingResponseDTO {

    private String ident;
    private Map<String, String> miljoStatus;

    private String error;
}