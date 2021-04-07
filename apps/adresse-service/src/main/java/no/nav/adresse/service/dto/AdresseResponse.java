package no.nav.adresse.service.dto;

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

    private List<PdlAdresseResponse.Vegadresse> vegadresser;
}
