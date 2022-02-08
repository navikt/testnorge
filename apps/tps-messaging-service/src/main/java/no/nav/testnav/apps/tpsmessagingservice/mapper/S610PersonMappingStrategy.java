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
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.SikkerhetstiltakDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.StatsborgerskapDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TelefonnummerDTO;
import no.nav.tps.ctg.s610.domain.EnkelBoAdresseType;
import no.nav.tps.ctg.s610.domain.S610BrukerType;
import no.nav.tps.ctg.s610.domain.S610PersonType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.testnav.libs.dto.tpsmessagingservice.v1.MidlertidigAdressetypeDTO.GATE;
import static no.nav.testnav.libs.dto.tpsmessagingservice.v1.MidlertidigAdressetypeDTO.PBOX;
import static no.nav.testnav.libs.dto.tpsmessagingservice.v1.MidlertidigAdressetypeDTO.STED;
import static no.nav.testnav.libs.dto.tpsmessagingservice.v1.MidlertidigAdressetypeDTO.UTAD;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class S610PersonMappingStrategy implements MappingStrategy {

    private static final String GATE_ADRESSE = "OFFA";
    private static final String MATR_ADRESSE = "MATR";
    private static final String BOLIGNR = "BOLIGNR: ";
    private static final String TPS = "TPS";

    public static LocalDateTime getTimestamp(String dato) {

        return isNotBlank(dato) ? LocalDate.parse(dato).atStartOfDay() : LocalDateTime.now();
    }

    public static SivilstandDTO getSivilstand(MapperFacade mapperFacade, S610PersonType.SivilstandDetalj sivilstand) {

        return nonNull(sivilstand) && nonNull(sivilstand.getKodeSivilstand()) ?
                mapperFacade.map(sivilstand, SivilstandDTO.class) : null;
    }

    private static MidlertidigAdresseDTO getNavAdresse(MapperFacade mapperFacade, S610BrukerType bruker) {

        if (nonNull(bruker) && nonNull(bruker.getPostadresseNorgeNAV()) &&
                isNotBlank(bruker.getPostadresseNorgeNAV().getTypeAdresseNavNorge())) {

            MidlertidigAdresseDTO midlertidigAdresse;

            if (STED.name().equals(bruker.getPostadresseNorgeNAV().getTypeAdresseNavNorge())) {
                midlertidigAdresse = MidlertidigAdresseDTO.MidlertidigStedAdresseDTO.builder()
                        .eiendomsnavn(bruker.getPostadresseNorgeNAV().getEiendomsnavn())
                        .build();
                midlertidigAdresse.setAdressetype(STED.name());
            } else if (PBOX.name().equals(bruker.getPostadresseNorgeNAV().getTypeAdresseNavNorge())) {
                midlertidigAdresse = MidlertidigAdresseDTO.MidlertidigPboxAdresseDTO.builder()
                        .postboksAnlegg(bruker.getPostadresseNorgeNAV().getPostboksAnlegg())
                        .postboksnr(bruker.getPostadresseNorgeNAV().getPostboksnr())
                        .build();
                midlertidigAdresse.setAdressetype(PBOX.name());
            } else {
                midlertidigAdresse = MidlertidigAdresseDTO.MidlertidigGateAdresseDTO.builder()
                        .gatenavn(bruker.getPostadresseNorgeNAV().getGatenavn())
                        .gatekode(bruker.getPostadresseNorgeNAV().getGatekode())
                        .husnr(bruker.getPostadresseNorgeNAV().getHusnr())
                        .build();
                midlertidigAdresse.setAdressetype(GATE.name());
            }
            midlertidigAdresse.setGyldigTom(getTimestamp(bruker.getPostadresseNorgeNAV().getDatoTom()));
            midlertidigAdresse.setPostnr(bruker.getPostadresseNorgeNAV().getPostnr());
            if (isNotBlank(bruker.getPostadresseNorgeNAV().getTilleggsLinje())) {
                midlertidigAdresse.setTilleggsadresse(bruker.getPostadresseNorgeNAV().getTilleggsLinje());
            } else if (isNotBlank(bruker.getPostadresseNorgeNAV().getBolignr())) {
                midlertidigAdresse.setTilleggsadresse(BOLIGNR + bruker.getPostadresseNorgeNAV().getBolignr());
            }
            return midlertidigAdresse;

        } else if (nonNull(bruker) && nonNull(bruker.getPostadresseUtlandNAV()) &&
                UTAD.name().equals(bruker.getPostadresseUtlandNAV().getAdresseType())) {

            var adresse = mapperFacade.map(bruker.getPostadresseUtlandNAV(),
                    MidlertidigAdresseDTO.MidlertidigUtadAdresseDTO.class);
            adresse.setGyldigTom(getTimestamp(bruker.getPostadresseUtlandNAV().getDatoTom()));
            adresse.setAdressetype(UTAD.name());
            return adresse;

        } else {
            return null;
        }
    }

    private static AdresseDTO getBoadresse(MapperFacade mapperFacade, EnkelBoAdresseType boadresse) {

        AdresseDTO adresse = null;
        if (nonNull(boadresse) && nonNull(boadresse.getFullBostedsAdresse()) &&
                GATE_ADRESSE.equals(boadresse.getFullBostedsAdresse().getAdresseType())) {

            adresse = mapperFacade.map(boadresse.getFullBostedsAdresse(), GateadresseDTO.class);

        } else if (nonNull(boadresse) && nonNull(boadresse.getFullBostedsAdresse()) &&
                MATR_ADRESSE.equals(boadresse.getFullBostedsAdresse().getAdresseType())) {

            adresse = mapperFacade.map(boadresse.getFullBostedsAdresse(), MatrikkeladresseDTO.class);
        }

        if (nonNull(adresse)) {

            if (isNotBlank(boadresse.getFullBostedsAdresse().getTilleggsAdresseSKD())) {
                adresse.setTilleggsadresse(boadresse.getFullBostedsAdresse().getTilleggsAdresseSKD());
            } else if (isNotBlank(boadresse.getFullBostedsAdresse().getBolignr())) {
                adresse.setTilleggsadresse(BOLIGNR + boadresse.getFullBostedsAdresse().getBolignr());
            }
        }

        return adresse;
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

    private static String getPersonStatus(S610PersonType tpsPerson) {
        return nonNull(tpsPerson.getPersonstatusDetalj()) && nonNull(tpsPerson.getPersonstatusDetalj().getKodePersonstatus()) ?
                tpsPerson.getPersonstatusDetalj().getKodePersonstatus().name() : null;
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
                        person.setStatsborgerskap(getStatsborgerskap(tpsPerson));
                        person.setDoedsdato(isNotBlank(tpsPerson.getDatoDo()) ?
                                LocalDate.parse(tpsPerson.getDatoDo()).atStartOfDay() : null);
                        person.setTknr(getTknr(tpsPerson.getBruker().getNAVenhetDetalj()));
                        person.setTknavn(getTknavn(tpsPerson.getBruker().getNAVenhetDetalj()));
                        person.setGtType(getGtType(tpsPerson.getBruker().getGeografiskTilknytning()));
                        person.setGtVerdi(getGtVerdi(tpsPerson.getBruker().getGeografiskTilknytning()));
                        person.setGtRegel(tpsPerson.getBruker().getRegelForGeografiskTilknytning());
                        person.setSprakKode(tpsPerson.getBruker().getPreferanser().getSprak());
                        person.setBankkontonrNorsk(getBankkontonrNorsk(tpsPerson));
                        person.setBankkontonrUtland(getBankkontonrUtland(tpsPerson));
                        person.setTelefonnumre(getTelefonnumre(tpsPerson));
                        person.setPersonStatus(getPersonStatus(tpsPerson));
                        person.setSikkerhetstiltak(getSikkerhetstiltak(tpsPerson));
                        person.setPostadresse(getPostadresse(tpsPerson));
                        person.setBoadresse(getBoadresse(mapperFacade, tpsPerson.getBostedsAdresse()));
                        person.setMidlertidigAdresse(getNavAdresse(mapperFacade, tpsPerson.getBruker()));
                        person.setSpesreg(tpsPerson.getBruker().getDiskresjonDetalj().getKodeDiskresjon());
                        person.setSpesregDato(getTimestamp(tpsPerson.getBruker().getDiskresjonDetalj().getDiskresjonTidspunkt()));
                        person.setEgenAnsattDatoFom(isNotBlank(tpsPerson.getBruker().getEgenAnsatt().getFom()) ?
                                LocalDate.parse(tpsPerson.getBruker().getEgenAnsatt().getFom()).atStartOfDay() : null);
                        person.setSivilstand(getSivilstand(mapperFacade, tpsPerson.getSivilstandDetalj()));
                        person.setImportFra(TPS);
                    }

                    private StatsborgerskapDTO getStatsborgerskap(S610PersonType tpsPerson) {
                        return nonNull(tpsPerson.getStatsborgerskapDetalj()) &&
                                isNotBlank(tpsPerson.getStatsborgerskapDetalj().getKodeStatsborgerskap()) ?
                                mapperFacade.map(tpsPerson.getStatsborgerskapDetalj(), StatsborgerskapDTO.class) : null;
                    }

                    private BankkontonrNorskDTO getBankkontonrNorsk(S610PersonType tpsPerson) {
                        return isNull(tpsPerson.getBankkontoNorge()) ||
                                isBlank(tpsPerson.getBankkontoNorge().getKontoNummer()) ? null :
                                mapperFacade.map(tpsPerson.getBankkontoNorge(), BankkontonrNorskDTO.class);
                    }

                    private BankkontonrUtlandDTO getBankkontonrUtland(S610PersonType tpsPerson) {
                        return nonNull(tpsPerson.getBruker()) && nonNull(tpsPerson.getBruker().getBankkontoUtland()) &&
                                isNotBlank(tpsPerson.getBruker().getBankkontoUtland().getGiroNrUtland()) ?
                                mapperFacade.map(tpsPerson.getBruker().getBankkontoUtland(), BankkontonrUtlandDTO.class) : null;
                    }

                    private List<TelefonnummerDTO> getTelefonnumre(S610PersonType tpsPerson) {
                        return nonNull(tpsPerson.getBruker()) && nonNull(tpsPerson.getBruker().getTelefoner()) &&
                                !tpsPerson.getBruker().getTelefoner().getTelefon().isEmpty() ?
                                mapperFacade.mapAsList(tpsPerson.getBruker().getTelefoner().getTelefon(), TelefonnummerDTO.class) : null;
                    }

                    private SikkerhetstiltakDTO getSikkerhetstiltak(S610PersonType tpsPerson) {
                        return nonNull(tpsPerson.getBruker()) && nonNull(tpsPerson.getBruker().getSikkerhetsTiltak()) &&
                                isNotBlank(tpsPerson.getBruker().getSikkerhetsTiltak().getTypeSikkerhetsTiltak()) ?
                                mapperFacade.map(tpsPerson.getBruker().getSikkerhetsTiltak(),
                                        SikkerhetstiltakDTO.class) : null;
                    }

                    private PostadresseDTO getPostadresse(S610PersonType tpsPerson) {
                        return nonNull(tpsPerson.getPostAdresse()) && nonNull(tpsPerson.getPostAdresse().getFullPostAdresse()) &&
                                isNotBlank(tpsPerson.getPostAdresse().getFullPostAdresse().getAdresse1()) ?
                                mapperFacade.map(tpsPerson.getPostAdresse().getFullPostAdresse(), PostadresseDTO.class) : null;
                    }
                })
                .exclude("statsborgerskap")
                .byDefault()
                .register();
    }

    private String getTknavn(S610BrukerType.NAVenhetDetalj naVenhetDetalj) {

        return nonNull(naVenhetDetalj) ? naVenhetDetalj.getKodeNAVenhetBeskr() : null;
    }
}
