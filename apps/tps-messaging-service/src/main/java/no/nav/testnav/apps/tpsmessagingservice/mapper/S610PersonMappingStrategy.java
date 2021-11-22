package no.nav.testnav.apps.tpsmessagingservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.AdresseDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrNorskDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.GateadresseDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.MidlertidigAdresseDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PostadresseDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.StatsborgerskapDTO;
import no.nav.tps.ctg.s610.domain.BoAdresseType;
import no.nav.tps.ctg.s610.domain.NavTIADType;
import no.nav.tps.ctg.s610.domain.PostAdresseType;
import no.nav.tps.ctg.s610.domain.S610BrukerType;
import no.nav.tps.ctg.s610.domain.S610PersonType;
import no.nav.tps.ctg.s610.domain.TelefonType;
import no.nav.tps.ctg.s610.domain.UtlandsAdresseType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.testnav.libs.dto.tpsmessagingservice.v1.MidlertidigAdressetypeDTO.PBOX;
import static no.nav.testnav.libs.dto.tpsmessagingservice.v1.MidlertidigAdressetypeDTO.STED;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class S610PersonMappingStrategy implements MappingStrategy {

    private static final String GATE_ADRESSE = "OFFA";
    private static final String MATR_ADRESSE = "MATR";
    private static final String BOLIGNR = "BOLIGNR: ";
    private static final String MOBIL = "MOBI";
    private static final String HJEM = "HJET";
    private static final String POST_UTLAND = "PUTL";
    private static final String POST_NORGE = "POST";
    private static final String NORGE = "NOR";
    private static final String TRUE = "J";
    private static final String TPS = "TPS";

    public static LocalDateTime getTimestamp(String dato) {

        return isNotBlank(dato) ? LocalDate.parse(dato).atStartOfDay() : LocalDateTime.now();
    }

    public static String getSivilstand(S610PersonType tpsPerson) {

        return nonNull(tpsPerson.getSivilstandDetalj()) && nonNull(tpsPerson.getSivilstandDetalj().getKodeSivilstand()) ?
                tpsPerson.getSivilstandDetalj().getKodeSivilstand().name() : null;
    }

    private static void mapTiadAdresse(S610PersonType tpsPerson, PersonDTO person) {

        NavTIADType tiadAdresse = tpsPerson.getBruker().getPostadresseNorgeNAV();

        if (nonNull(tiadAdresse) && isNotBlank(tiadAdresse.getTypeAdresseNavNorge())) {
            MidlertidigAdresseDTO midlertidigAdresse;

            if (STED.name().equals(tiadAdresse.getTypeAdresseNavNorge())) {
                midlertidigAdresse = MidlertidigAdresseDTO.MidlertidigStedAdresseDTO.builder()
                        .eiendomsnavn(tiadAdresse.getEiendomsnavn())
                        .build();
            } else if (PBOX.name().equals(tiadAdresse.getTypeAdresseNavNorge())) {
                midlertidigAdresse = MidlertidigAdresseDTO.MidlertidigPboxAdresseDTO.builder()
                        .postboksAnlegg(tiadAdresse.getPostboksAnlegg())
                        .postboksnr(tiadAdresse.getPostboksnr())
                        .build();
            } else {
                midlertidigAdresse = MidlertidigAdresseDTO.MidlertidigGateAdresseDTO.builder()
                        .gatenavn(tiadAdresse.getGatenavn())
                        .gatekode(tiadAdresse.getGatekode())
                        .husnr(tiadAdresse.getHusnr())
                        .build();
            }
            midlertidigAdresse.setGyldigTom(getTimestamp(tiadAdresse.getDatoTom()));
            midlertidigAdresse.setPostnr(tiadAdresse.getPostnr());
            if (isNotBlank(tiadAdresse.getTilleggsLinje())) {
                midlertidigAdresse.setTilleggsadresse(tiadAdresse.getTilleggsLinje());
            } else if (isNotBlank(tiadAdresse.getBolignr())) {
                midlertidigAdresse.setTilleggsadresse(BOLIGNR + tiadAdresse.getBolignr());
            }
            person.getMidlertidigAdresse().add(midlertidigAdresse);
        }
    }

    private static void mapUtadAdresse(S610PersonType tpsPerson, PersonDTO person) {

        UtlandsAdresseType utenlandsadresse = tpsPerson.getBruker().getPostadresseUtlandNAV();

        if (nonNull(utenlandsadresse) && isNotBlank(utenlandsadresse.getAdresseType())) {
            var midlertidigAdresse = MidlertidigAdresseDTO.MidlertidigUtadAdresseDTO.builder()
                    .postLinje1(utenlandsadresse.getAdresse1())
                    .postLinje2(utenlandsadresse.getAdresse2())
                    .postLinje3(utenlandsadresse.getAdresse3())
                    .postLand(utenlandsadresse.getLandKode())
                    .build();
            person.getMidlertidigAdresse().add(midlertidigAdresse);
        }
    }

    private static void mapPostadresse(S610PersonType tpsPerson, PersonDTO person) {

        PostAdresseType tpsPostadresse = tpsPerson.getPostAdresse().getFullPostAdresse();

        if (isNotBlank(tpsPostadresse.getAdresseType())) {

            var postadresse = PostadresseDTO.builder()
                    .postLinje1(tpsPostadresse.getAdresse1())
                    .postLinje2(tpsPostadresse.getAdresse2())
                    .postLinje3(tpsPostadresse.getAdresse3())
                    .person(person)
                    .build();

            if (POST_NORGE.equals(tpsPostadresse.getAdresseType())) {

                String poststed = format("%s %s", tpsPostadresse.getPostnr(), tpsPostadresse.getPoststed());
                if (isBlank(postadresse.getPostLinje1())) {
                    postadresse.setPostLinje1(poststed);
                } else if (isBlank(postadresse.getPostLinje2())) {
                    postadresse.setPostLinje2(poststed);
                } else if (isBlank(postadresse.getPostLinje3())) {
                    postadresse.setPostLinje3(poststed);
                }
                postadresse.setPostLand(NORGE);

            } else if (POST_UTLAND.equals(tpsPostadresse.getAdresseType())) {

                postadresse.setPostLand(tpsPostadresse.getLandKode());
            }
            person.getPostadresse().add(postadresse);
        }
    }

    private static void mapBoadresse(S610PersonType tpsPerson, PersonDTO person) {

        BoAdresseType tpsBoadresse = tpsPerson.getBostedsAdresse().getFullBostedsAdresse();

        AdresseDTO adresse = null;
        if (GATE_ADRESSE.equals(tpsBoadresse.getAdresseType())) {

            adresse = GateadresseDTO.builder()
                    .adresse(tpsBoadresse.getOffAdresse().getGateNavn())
                    .husnummer(skipLeadZeros(tpsBoadresse.getOffAdresse().getHusnr()))
                    .gatekode(tpsBoadresse.getOffAdresse().getGatekode())
                    .build();

        } else if (MATR_ADRESSE.equals(tpsBoadresse.getAdresseType())) {

            adresse = MatrikkeladresseDTO.builder()
                    .mellomnavn(tpsBoadresse.getMatrAdresse().getMellomAdresse())
                    .gardsnr(skipLeadZeros(tpsBoadresse.getMatrAdresse().getGardsnr()))
                    .bruksnr(skipLeadZeros(tpsBoadresse.getMatrAdresse().getBruksnr()))
                    .festenr(skipLeadZeros(tpsBoadresse.getMatrAdresse().getFestenr()))
                    .undernr(skipLeadZeros(tpsBoadresse.getMatrAdresse().getUndernr()))
                    .build();
        }

        if (nonNull(adresse)) {

            if (isNotBlank(tpsBoadresse.getTilleggsAdresseSKD())) {
                adresse.setTilleggsadresse(tpsBoadresse.getTilleggsAdresseSKD());
            } else if (isNotBlank(tpsBoadresse.getBolignr())) {
                adresse.setTilleggsadresse(BOLIGNR + tpsBoadresse.getBolignr());
            }
            adresse.setPostnr(tpsBoadresse.getPostnr());
            adresse.setKommunenr(tpsBoadresse.getKommunenr());
            person.getBoadresse().add(adresse);
        }
    }

    private static String skipLeadZeros(String number) {

        return StringUtils.isNumeric(number) ?
                Integer.valueOf(number).toString() :
                number;
    }

    private static void fixTelefonnr(PersonDTO person) {

        if (isBlank(person.getTelefonnummer_1()) && isNotBlank(person.getTelefonnummer_2())) {
            person.setTelefonnummer_1(person.getTelefonnummer_2());
            person.setTelefonLandskode_1(person.getTelefonLandskode_2());
            person.setTelefonnummer_2(null);
            person.setTelefonLandskode_2(null);
        }
    }

    private static String getTlfnrLandskode(S610BrukerType.Telefoner telefoner, String telefontype) {

        return nonNull(telefoner) ? telefoner.getTelefon().stream()
                .filter(telefon -> telefontype.equals(telefon.getTlfType()))
                .map(telefon ->
                        isNotBlank(telefon.getTlfLandkode()) ?
                                telefon.getTlfLandkode() : "+47"
                )
                .findFirst().orElse(null) : null;
    }

    private static String getTknr(S610BrukerType.NAVenhetDetalj naVenhetDetalj) {

        return nonNull(naVenhetDetalj) ? naVenhetDetalj.getKodeNAVenhet() : null;
    }

    private static String getGtType(S610BrukerType.GeografiskTilknytning geoTilknytning) {

        if (nonNull(geoTilknytning)) {
            if (isNotBlank(geoTilknytning.getKommunenr())) {
                return "KNR";
            } else if (isNotBlank(geoTilknytning.getLandKode())) {
                return "LAND";
            } else if (isNotBlank(geoTilknytning.getBydel())) {
                return "BYDEL";
            }
        }
        return null;
    }

    private static String getGtVerdi(S610BrukerType.GeografiskTilknytning geoTilknytning) {

        if (nonNull(geoTilknytning)) {
            if (isNotBlank(geoTilknytning.getKommunenr())) {
                return geoTilknytning.getKommunenr();
            } else if (isNotBlank(geoTilknytning.getLandKode())) {
                return geoTilknytning.getLandKode();
            } else if (isNotBlank(geoTilknytning.getBydel())) {
                return geoTilknytning.getBydel();
            }
        }
        return null;
    }

    private BankkontonrUtlandDTO getBankkontonrUtland(MapperFacade mapperFacade, S610BrukerType brukerType) {

        return nonNull(brukerType) && nonNull(brukerType.getBankkontoUtland()) ?
                mapperFacade.map(brukerType.getBankkontoUtland(), BankkontonrUtlandDTO.class) :
                null;
    }

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(S610PersonType.class, PersonDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(S610PersonType tpsPerson,
                                        PersonDTO person, MappingContext context) {

                        person.setIdent(tpsPerson.getFodselsnummer());
                        person.setIdenttype(tpsPerson.getIdentType());
                        person.setFornavn(tpsPerson.getPersonnavn().getAllePersonnavn().getFornavn());
                        person.setMellomnavn(tpsPerson.getPersonnavn().getAllePersonnavn().getMellomnavn());
                        person.setEtternavn(tpsPerson.getPersonnavn().getAllePersonnavn().getEtternavn());
                        person.setForkortetNavn(tpsPerson.getPersonnavn().getAllePersonnavn().getKortnavn());
                        person.getStatsborgerskap().add(StatsborgerskapDTO.builder()
                                .statsborgerskap(tpsPerson.getStatsborgerskapDetalj().getKodeStatsborgerskap())
                                .statsborgerskapRegdato(getTimestamp(tpsPerson.getStatsborgerskapDetalj().getDatoStatsborgerskap()))
                                .person(person)
                                .build());
                        person.setDoedsdato(isNotBlank(tpsPerson.getDatoDo()) ?
                                LocalDate.parse(tpsPerson.getDatoDo()).atStartOfDay() : null);
                        person.setTknr(getTknr(tpsPerson.getBruker().getNAVenhetDetalj()));
                        person.setTknavn(getTknavn(tpsPerson.getBruker().getNAVenhetDetalj()));
                        person.setGtType(getGtType(tpsPerson.getBruker().getGeografiskTilknytning()));
                        person.setGtVerdi(getGtVerdi(tpsPerson.getBruker().getGeografiskTilknytning()));
                        person.setGtRegel(tpsPerson.getBruker().getRegelForGeografiskTilknytning());
                        person.setSprakKode(tpsPerson.getBruker().getPreferanser().getSprak());
                        person.setBankkontonrNorsk(isNull(person.getBankkontonrNorsk()) ? null:
                                mapperFacade.map(person, BankkontonrNorskDTO.class));
                        person.setBankkontonrUtland(getBankkontonrUtland(mapperFacade, tpsPerson.getBruker()));
                        person.setTelefonLandskode_1(getTlfnrLandskode(tpsPerson.getBruker().getTelefoner(), MOBIL));
                        person.setTelefonnummer_1(getTelefonnr(tpsPerson.getBruker().getTelefoner(), MOBIL));
                        person.setTelefonLandskode_2(getTlfnrLandskode(tpsPerson.getBruker().getTelefoner(), HJEM));
                        person.setTelefonnummer_2(getTelefonnr(tpsPerson.getBruker().getTelefoner(), HJEM));
                        fixTelefonnr(person);
                        person.setPersonStatus(tpsPerson.getPersonstatusDetalj().getKodePersonstatus().name());
                        person.setBeskrSikkerhetTiltak(tpsPerson.getBruker().getSikkerhetsTiltak().getBeskrSikkerhetsTiltak());
                        person.setTypeSikkerhetTiltak(tpsPerson.getBruker().getSikkerhetsTiltak().getTypeSikkerhetsTiltak());
                        person.setSikkerhetTiltakDatoFom(getTimestamp(tpsPerson.getBruker().getSikkerhetsTiltak().getSikrFom()));
                        person.setSikkerhetTiltakDatoTom(getTimestamp(tpsPerson.getBruker().getSikkerhetsTiltak().getSikrTom()));
                        mapBoadresse(tpsPerson, person);
                        mapPostadresse(tpsPerson, person);
                        mapUtadAdresse(tpsPerson, person);
                        mapTiadAdresse(tpsPerson, person);
                        person.setSpesreg(tpsPerson.getBruker().getDiskresjonDetalj().getKodeDiskresjon());
                        person.setSpesregDato(getTimestamp(tpsPerson.getBruker().getDiskresjonDetalj().getDiskresjonTidspunkt()));
                        person.setEgenAnsattDatoFom(isNotBlank(tpsPerson.getBruker().getEgenAnsatt().getFom()) ?
                                LocalDate.parse(tpsPerson.getBruker().getEgenAnsatt().getFom()).atStartOfDay() : null);
                        person.setSivilstand(getSivilstand(tpsPerson));
                        person.setSivilstandRegdato(getTimestamp(tpsPerson.getSivilstandDetalj().getSivilstTidspunkt()));
                        person.setImportFra(TPS);
                    }

                })
                .exclude("statsborgerskap")
                .byDefault()
                .register();
    }

    private String getTelefonnr(S610BrukerType.Telefoner telefoner, String telefontype) {

        return nonNull(telefoner) ? telefoner.getTelefon().stream()
                .filter(telefon -> telefontype.equals(telefon.getTlfType()))
                .map(TelefonType::getTlfNummer)
                .findFirst().orElse(null) : null;
    }

    private String getTknavn(S610BrukerType.NAVenhetDetalj naVenhetDetalj) {

        return nonNull(naVenhetDetalj) ? naVenhetDetalj.getKodeNAVenhetBeskr() : null;
    }
}
