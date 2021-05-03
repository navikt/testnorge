package no.nav.pdl.forvalter.artifact;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.consumer.AddressServiceConsumer;
import no.nav.pdl.forvalter.domain.PdlVegadresse;
import no.nav.pdl.forvalter.dto.AdresseRequest;
import no.nav.pdl.forvalter.dto.PdlAdresseResponse;
import org.springframework.stereotype.Service;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class VegadresseService {

    private final AddressServiceConsumer addressServiceConsumer;

    public PdlAdresseResponse.Vegadresse get(PdlVegadresse vegadresse, String adresseIdentifikatorFraMatrikkelen) {

        if (isNotBlank(adresseIdentifikatorFraMatrikkelen)) {

            return addressServiceConsumer.getAdresserAuto(AdresseRequest.builder()
                    .matrikkelId(adresseIdentifikatorFraMatrikkelen)
                    .build());
        }
        return null;
    }
}
