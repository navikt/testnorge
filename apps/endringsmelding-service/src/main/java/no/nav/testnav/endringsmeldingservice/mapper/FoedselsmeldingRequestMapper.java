package no.nav.testnav.endringsmeldingservice.mapper;

import lombok.experimental.UtilityClass;
import no.nav.testnav.endringsmeldingservice.utility.KjoennFraIdentUtility;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.AdresseDTO;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.AdressehistorikkDTO;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.FoedselsmeldingRequest;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.GateadresseDTO;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.PersonDTO;
import no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO;
import no.nav.testnav.libs.dto.endringsmelding.v2.FoedselsmeldingDTO;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import reactor.util.function.Tuple5;

import java.util.List;

import static java.util.Objects.nonNull;
import static no.nav.testnav.endringsmeldingservice.mapper.AdressehistorikkMapper.mapBoadresse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class FoedselsmeldingRequestMapper {

    public static FoedselsmeldingRequest map(FoedselsmeldingDTO fodselsmelding, Tuple5<List<String>, List<NavnDTO>, List<VegadresseDTO>,
            List<AdressehistorikkDTO>, List<AdressehistorikkDTO>> opplysninger) {

        return FoedselsmeldingRequest.builder()
                .barn(PersonDTO.builder()
                        .ident(opplysninger.getT1().getFirst())
                        .identtype(fodselsmelding.getIdenttype().name())
                        .fornavn(opplysninger.getT2().getFirst().getAdjektiv())
                        .mellomnavn(opplysninger.getT2().getFirst().getAdverb())
                        .etternavn(opplysninger.getT2().getFirst().getSubstantiv())
                        .boadresse(mapAdresse(fodselsmelding, opplysninger.getT3(), opplysninger.getT4(), opplysninger.getT5()))
                        .kjonn(KjoennFraIdentUtility.get(opplysninger.getT1().getFirst()))
                        .build())
                .mor(PersonDTO.builder()
                        .ident(fodselsmelding.getIdentMor())
                        .build())
                .far(isNotBlank(fodselsmelding.getIdentFar()) ?
                        PersonDTO.builder()
                                .ident(fodselsmelding.getIdentFar())
                                .build() : null)
                .build();
    }

    private static AdresseDTO mapAdresse(FoedselsmeldingDTO foedselsmelding, List<VegadresseDTO> vegadresse,
                                         List<AdressehistorikkDTO> morsadresser, List<AdressehistorikkDTO> farsadresser) {

        return switch (foedselsmelding.getAdresseFra()) {

            case LAG_NY_ADRESSE -> mapAdresse(vegadresse.getFirst());
            case ARV_FRA_MORS -> mapBoadresse(morsadresser.getFirst().getPersondata());
            case ARV_FRA_FARS -> nonNull(farsadresser) && !farsadresser.isEmpty() &&
                    nonNull(farsadresser.getFirst().getPersondata()) ?
                    mapBoadresse(farsadresser.getFirst().getPersondata()) :
                    mapAdresse(vegadresse.getFirst());
        };
    }

    private static AdresseDTO mapAdresse(VegadresseDTO vegadresse) {

        return GateadresseDTO.builder()
                .adresse(vegadresse.getAdressenavn())
                .gatekode(vegadresse.getAdressekode())
                .husnummer(vegadresse.getHusnummer().toString())
                .husbokstav(vegadresse.getHusbokstav())
                .postnr(vegadresse.getPostnummer())
                .kommunenr(vegadresse.getKommunenummer())
                .tilleggsadresse(vegadresse.getTilleggsnavn())
                .build();
    }
}
