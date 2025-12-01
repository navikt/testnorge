package no.nav.testnav.endringsmeldingservice.mapper;

import lombok.experimental.UtilityClass;
import no.nav.testnav.endringsmeldingservice.utility.KjoennFraIdentUtility;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.AdresseDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.AdressehistorikkDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.FoedselsmeldingRequest;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.GateadresseDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PostadresseDTO;
import no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO;
import no.nav.testnav.libs.dto.endringsmelding.v2.FoedselsmeldingDTO;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import reactor.util.function.Tuple5;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.testnav.endringsmeldingservice.mapper.AdressehistorikkMapper.mapBoadresse;
import static no.nav.testnav.endringsmeldingservice.mapper.AdressehistorikkMapper.mapPostBoadresse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class FoedselsmeldingRequestMapper {

    public static FoedselsmeldingRequest map(FoedselsmeldingDTO foedselsmelding, Tuple5<List<String>, List<NavnDTO>, List<VegadresseDTO>,
            List<AdressehistorikkDTO>, List<AdressehistorikkDTO>> opplysninger) {

        return FoedselsmeldingRequest.builder()
                .barn(PersonDTO.builder()
                        .ident(opplysninger.getT1().getFirst())
                        .identtype(foedselsmelding.getIdenttype().name())
                        .fornavn(opplysninger.getT2().getFirst().getAdjektiv())
                        .mellomnavn(opplysninger.getT2().getFirst().getAdverb())
                        .etternavn(opplysninger.getT2().getFirst().getSubstantiv())
                        .boadresse(mapAdresse(foedselsmelding, opplysninger.getT3(), opplysninger.getT4(), opplysninger.getT5()))
                        .postadresse(mapPostadresse(foedselsmelding, opplysninger.getT3(), opplysninger.getT4(), opplysninger.getT5()))
                        .kjonn(KjoennFraIdentUtility.get(opplysninger.getT1().getFirst()))
                        .build())
                .mor(PersonDTO.builder()
                        .ident(foedselsmelding.getIdentMor())
                        .build())
                .far(isNotBlank(foedselsmelding.getIdentFar()) ?
                        PersonDTO.builder()
                                .ident(foedselsmelding.getIdentFar())
                                .build() : null)
                .build();
    }

    private static PostadresseDTO mapPostadresse(FoedselsmeldingDTO foedselsmelding, List<VegadresseDTO> vegadresse,
                                                 List<AdressehistorikkDTO> morsadresser, List<AdressehistorikkDTO> farsadresser) {

        if (isNull(foedselsmelding.getAdresseFra())) {
            return mapPostadresse(vegadresse.getFirst());
        }

        return switch (foedselsmelding.getAdresseFra()) {

            case LAG_NY_ADRESSE -> mapPostadresse(vegadresse.getFirst());
            case ARV_FRA_MORS -> mapPostBoadresse(morsadresser.getFirst().getPersondata());
            case ARV_FRA_FARS -> nonNull(farsadresser) && !farsadresser.isEmpty() &&
                    nonNull(farsadresser.getFirst().getPersondata()) ?
                    mapPostBoadresse(farsadresser.getFirst().getPersondata()) :
                    mapPostadresse(vegadresse.getFirst());
        };
    }

    private static AdresseDTO mapAdresse(FoedselsmeldingDTO foedselsmelding, List<VegadresseDTO> vegadresse,
                                         List<AdressehistorikkDTO> morsadresser, List<AdressehistorikkDTO> farsadresser) {

        if (isNull(foedselsmelding.getAdresseFra())) {
            return mapAdresse(vegadresse.getFirst());
        }

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
                .poststed(vegadresse.getPoststed())
                .kommunenr(vegadresse.getKommunenummer())
                .kommuneNavn(vegadresse.getKommunenavn())
                .tilleggsadresse(vegadresse.getTilleggsnavn())
                .build();
    }

    private static PostadresseDTO mapPostadresse(VegadresseDTO vegadresse) {

        return PostadresseDTO.builder()
                .postLinje1("%s %s%s".formatted(vegadresse.getAdressenavn(), vegadresse.getHusnummer().toString(),
                        Objects.toString(vegadresse.getHusbokstav(), "")))
                .postLinje2("%s %s".formatted(vegadresse.getPostnummer(), vegadresse.getPoststed()))
                .postLand("NOR")
                .build();
    }
}
