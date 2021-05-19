package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
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
    private final MapperFacade mapperFacade;

    public PdlAdresseResponse.Vegadresse get(PdlVegadresse vegadresse, String adresseIdentifikatorFraMatrikkelen) {

        if (isNotBlank(adresseIdentifikatorFraMatrikkelen)) {

            return addressServiceConsumer.getAdresserAuto(AdresseRequest.builder()
                    .matrikkelId(adresseIdentifikatorFraMatrikkelen)
                    .build());

        } else if (isNotBlank(vegadresse.getAdressenavn())) {

            return mapperFacade.map(vegadresse, PdlAdresseResponse.Vegadresse.class);

        } else if (isNotBlank(vegadresse.getPostnummer())) {

            return addressServiceConsumer.getAdresseFromPostnummer(vegadresse.getPostnummer());

        } else if (isNotBlank(vegadresse.getKommunenummer())) {

            return addressServiceConsumer.getAdresseFromKommunenummer(vegadresse.getKommunenummer());

        } else {

            return addressServiceConsumer.getAdresserAuto(new AdresseRequest());
        }
    }
}
