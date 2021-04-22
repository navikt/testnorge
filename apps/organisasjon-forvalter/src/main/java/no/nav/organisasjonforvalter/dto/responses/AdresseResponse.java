package no.nav.organisasjonforvalter.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdresseResponse {

    private List<PdlVegAdresse> vegadresser;
}
