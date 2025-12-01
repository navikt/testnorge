package no.nav.testnav.endringsmeldingservice.mapper;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.AdresseDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.AdressehistorikkDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.AdressehistorikkRequest;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.GateadresseDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonMiljoeDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PostadresseDTO;

import java.time.LocalDate;

@UtilityClass
public class AdressehistorikkMapper {

    public static PersonDTO mapHistorikk(AdressehistorikkDTO.PersonData person) {

        return PersonDTO.builder()
                .ident(person.getIdent())
                .identtype(person.getIdentType())
                .boadresse(mapBoadresse(person))
                .postadresse(person.getPostAdresse().stream()
                        .map(postadresse -> PostadresseDTO.builder()
                                .postLinje1(postadresse.getAdresse1())
                                .postLinje2(postadresse.getAdresse2())
                                .postLinje3(postadresse.getAdresse3())
                                .postLand(postadresse.getLand())
                                .build()
                        )
                        .findFirst()
                        .orElse(null)
                )
                .build();
    }

    public static AdresseDTO mapBoadresse(AdressehistorikkDTO.PersonData person) {

        return (AdresseDTO) person.getBostedsAdresse().stream()
                .map(boadresse -> {

                    if ("OFFA".equals(boadresse.getAdresseType())) {
                        return GateadresseDTO.builder()
                                .gatekode(boadresse.getOffAdresse().getGatekode())
                                .adresse(boadresse.getOffAdresse().getGateNavn())
                                .husnummer(boadresse.getOffAdresse().getHusnr())
                                .husbokstav(boadresse.getOffAdresse().getBokstav())
                                .bolignr(boadresse.getBolignr())
                                .flyttedato(boadresse.getDatoFom().atStartOfDay())
                                .postnr(boadresse.getPostnr())
                                .kommunenr(boadresse.getKommunenr())
                                .tilleggsadresse(boadresse.getTilleggsAdresseSKD())
                                .build();
                    } else {

                        return MatrikkeladresseDTO.builder()
                                .mellomnavn(boadresse.getMatrAdresse().getMellomAdresse())
                                .gardsnr(boadresse.getMatrAdresse().getGardsnr())
                                .bruksnr(boadresse.getMatrAdresse().getBruksnr())
                                .festenr(boadresse.getMatrAdresse().getFestenr())
                                .undernr(boadresse.getMatrAdresse().getUndernr())
                                .bolignr(boadresse.getBolignr())
                                .flyttedato(boadresse.getDatoFom().atStartOfDay())
                                .postnr(boadresse.getPostnr())
                                .kommunenr(boadresse.getKommunenr())
                                .tilleggsadresse(boadresse.getTilleggsAdresseSKD());
                    }
                })
                .findFirst()
                .orElse(null);
    }

    public static PostadresseDTO mapPostBoadresse(AdressehistorikkDTO.PersonData person) {

        return (PostadresseDTO) person.getBostedsAdresse().stream()
                .map(boadresse -> {

                    if ("OFFA".equals(boadresse.getAdresseType())) {
                        return PostadresseDTO.builder()
                                .postLinje1("%s %s%s".formatted(boadresse.getOffAdresse().getGateNavn(),
                                        boadresse.getOffAdresse().getHusnr(),
                                        boadresse.getOffAdresse().getBokstav()))
                                .postLinje2("%s %s".formatted(boadresse.getPostnr(), boadresse.getPoststed()))
                                .postLand("NOR")
                                .build();
                    } else {

                        return PostadresseDTO.builder()
                                .postLinje1("Gardsnummer: " + boadresse.getMatrAdresse().getGardsnr())
                                .postLinje2("Bruksnummer: " + boadresse.getMatrAdresse().getBruksnr())
                                .postLinje3("Kommunenummer: " + boadresse.getKommunenr())
                                .postLand("NOR");
                    }
                })
                .findFirst()
                .orElse(null);
    }

    public static AdressehistorikkRequest buildAdresseRequest(PersonMiljoeDTO person) {

        return AdressehistorikkRequest.builder()
                .ident(person.getIdent())
                .aksjonsdato(person.getPerson().getDoedsdato().toLocalDate().minusDays(1))
                .build();
    }

    public static AdressehistorikkRequest buildAdresseRequest(String ident, LocalDate aksjonsdato) {

        return AdressehistorikkRequest.builder()
                .ident(ident)
                .aksjonsdato(aksjonsdato)
                .build();
    }
}
