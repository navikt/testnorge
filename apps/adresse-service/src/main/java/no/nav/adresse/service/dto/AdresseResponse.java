package no.nav.adresse.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdresseResponse {

    private List<PdlAdresseResponse.Vegadresse> vegadresse;
}
