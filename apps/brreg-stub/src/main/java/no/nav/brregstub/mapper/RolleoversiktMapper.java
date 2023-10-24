package no.nav.brregstub.mapper;

import lombok.SneakyThrows;
import no.nav.brregstub.api.common.RolleKode;
import no.nav.brregstub.api.common.RsAdresse;
import no.nav.brregstub.api.common.RsNavn;
import no.nav.brregstub.api.v1.RolleTo;
import no.nav.brregstub.api.v1.RolleoversiktTo;
import no.nav.brregstub.api.v2.RsRolle;
import no.nav.brregstub.api.v2.RsRolleoversikt;
import no.nav.brregstub.generated.AdresseType1;
import no.nav.brregstub.generated.AdresseType2;
import no.nav.brregstub.generated.GrunndataUtskrift;
import no.nav.brregstub.generated.NavnType;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static no.nav.brregstub.api.common.UnderstatusKode.understatusKoder;

public class RolleoversiktMapper {

    public static final String TJENESTE_NAVN = "hentRolleutskrift";

    public static GrunndataUtskrift map(RolleoversiktTo to) {
        var grunndata = new GrunndataUtskrift();
        var responseHeader = mapTilResponseHeader(to);
        grunndata.setResponseHeader(responseHeader);

        if (to.getHovedstatus() == 0) {
            var melding = mapTilMelding(to);
            grunndata.setMelding(melding);
        }

        return grunndata;
    }

    public static GrunndataUtskrift map(RsRolleoversikt rsRolleoversikt) {
        var grunndata = new GrunndataUtskrift();
        var responseHeader = mapTilResponseHeader(rsRolleoversikt);
        grunndata.setResponseHeader(responseHeader);

        if (rsRolleoversikt.getHovedstatus() == 0) {
            var melding = mapTilMelding(rsRolleoversikt);
            grunndata.setMelding(melding);
        }

        return grunndata;
    }

    public static GrunndataUtskrift.Melding.Roller.Enhet.RolleBeskrivelse mapTilRollebeskrivelse(String beskrivelseTo) {
        var beskrivelse = new GrunndataUtskrift.Melding.Roller.Enhet.RolleBeskrivelse();
        beskrivelse.setValue(beskrivelseTo);
        beskrivelse.setLedetekst("Rolle");
        return beskrivelse;
    }

    public static GrunndataUtskrift.Melding.Roller.Enhet.RolleBeskrivelse mapTilRollebeskrivelse(RolleKode rolleKode) {
        var beskrivelse = new GrunndataUtskrift.Melding.Roller.Enhet.RolleBeskrivelse();
        beskrivelse.setValue(rolleKode.getBeskrivelse());
        beskrivelse.setLedetekst("Rolle");
        return beskrivelse;
    }

    public static GrunndataUtskrift.Melding.Roller.Enhet.Orgnr mapTilOrganisasjonsNummer(Integer orgnrTo) {
        var orgnr = new GrunndataUtskrift.Melding.Roller.Enhet.Orgnr();
        orgnr.setValue(orgnrTo);
        return orgnr;
    }

    public static NavnType mapTilNavntype(RsNavn rsNavn) {
        var navn = new NavnType();
        navn.setNavn1(rsNavn.getNavn1());
        navn.setNavn2(rsNavn.getNavn2());
        navn.setNavn3(rsNavn.getNavn3());
        return navn;
    }

    private static GrunndataUtskrift.ResponseHeader mapTilResponseHeader(RolleoversiktTo to) {
        var responseHeader = new GrunndataUtskrift.ResponseHeader();
        responseHeader.setProssessDato(localDateToXmlGregorianCalendar(LocalDate.now()));
        responseHeader.setTjeneste(TJENESTE_NAVN);
        responseHeader.setFodselsnr(to.getFnr());
        responseHeader.setHovedStatus(to.getHovedstatus());
        var underStatus = new GrunndataUtskrift.ResponseHeader.UnderStatus();
        if (to.getUnderstatuser().isEmpty()) {
            var underStatusMelding = new GrunndataUtskrift.ResponseHeader.UnderStatus.UnderStatusMelding();
            underStatusMelding.setKode(0);
            underStatusMelding.setValue(understatusKoder.get(0));

            underStatus.getUnderStatusMelding().add(underStatusMelding);
        } else {
            to.getUnderstatuser().forEach(understatus -> {
                var underStatusMelding = new GrunndataUtskrift.ResponseHeader.UnderStatus.UnderStatusMelding();
                underStatusMelding.setKode(understatus);
                underStatusMelding.setValue(understatusKoder.get(understatus));
                underStatus.getUnderStatusMelding().add(underStatusMelding);
            });
        }

        responseHeader.setUnderStatus(underStatus);
        return responseHeader;
    }

    private static GrunndataUtskrift.ResponseHeader mapTilResponseHeader(RsRolleoversikt rsRolleoversikt) {
        var responseHeader = new GrunndataUtskrift.ResponseHeader();
        responseHeader.setProssessDato(localDateToXmlGregorianCalendar(LocalDate.now()));
        responseHeader.setTjeneste(TJENESTE_NAVN);
        responseHeader.setFodselsnr(rsRolleoversikt.getFnr());
        responseHeader.setHovedStatus(rsRolleoversikt.getHovedstatus());
        var underStatus = new GrunndataUtskrift.ResponseHeader.UnderStatus();
        if (rsRolleoversikt.getUnderstatuser().isEmpty()) {
            var underStatusMelding = new GrunndataUtskrift.ResponseHeader.UnderStatus.UnderStatusMelding();
            underStatusMelding.setKode(0);
            underStatusMelding.setValue(understatusKoder.get(0));

            underStatus.getUnderStatusMelding().add(underStatusMelding);
        } else {
            rsRolleoversikt.getUnderstatuser().forEach(understatus -> {
                var underStatusMelding = new GrunndataUtskrift.ResponseHeader.UnderStatus.UnderStatusMelding();
                underStatusMelding.setKode(understatus);
                underStatusMelding.setValue(understatusKoder.get(understatus));
                underStatus.getUnderStatusMelding().add(underStatusMelding);
            });
        }

        responseHeader.setUnderStatus(underStatus);
        return responseHeader;
    }

    private static GrunndataUtskrift.Melding mapTilMelding(RolleoversiktTo to) {
        var melding = new GrunndataUtskrift.Melding();
        melding.setRolleInnehaver(mapTilRolleInnhaver(to));
        melding.setRoller(mapTilRoller(to));
        melding.setTjeneste(TJENESTE_NAVN);

        return melding;
    }

    private static GrunndataUtskrift.Melding mapTilMelding(RsRolleoversikt rsRolleoversikt) {
        var melding = new GrunndataUtskrift.Melding();
        melding.setRolleInnehaver(mapTilRolleInnhaver(rsRolleoversikt));
        melding.setRoller(mapTilRoller(rsRolleoversikt));
        melding.setTjeneste(TJENESTE_NAVN);

        return melding;
    }

    private static GrunndataUtskrift.Melding.Roller mapTilRoller(RolleoversiktTo to) {
        var roller = new GrunndataUtskrift.Melding.Roller();
        if (to.getEnheter() != null) {
            int count = 1;
            for (RolleTo enhetTo : to.getEnheter()) {
                var enhet = new GrunndataUtskrift.Melding.Roller.Enhet();
                enhet.setRolleBeskrivelse(mapTilRollebeskrivelse(enhetTo.getRollebeskrivelse()));
                enhet.setNr(count);
                count++;
                enhet.setNavn(mapTilNavntype(enhetTo.getForetaksNavn()));
                enhet.setOrgnr(mapTilOrganisasjonsNummer(enhetTo.getOrgNr()));
                enhet.setAdresse(mapTilAdresseEnhet(enhetTo));
                enhet.setRegistreringsDato(localDateToXmlGregorianCalendar(enhetTo.getRegistreringsdato()));
                roller.getEnhet().add(enhet);
            }
        }
        return roller;
    }

    private static GrunndataUtskrift.Melding.Roller mapTilRoller(RsRolleoversikt rsRolleoversikt) {
        var roller = new GrunndataUtskrift.Melding.Roller();
        if (rsRolleoversikt.getEnheter() != null) {
            int count = 1;
            for (RsRolle rsRolle : rsRolleoversikt.getEnheter()) {
                var enhet = new GrunndataUtskrift.Melding.Roller.Enhet();
                enhet.setRolleBeskrivelse(mapTilRollebeskrivelse(rsRolle.getRolle()));
                enhet.setNr(count);
                count++;
                enhet.setNavn(mapTilNavntype(rsRolle.getForetaksNavn()));
                enhet.setOrgnr(mapTilOrganisasjonsNummer(rsRolle.getOrgNr()));
                enhet.setAdresse(mapTilAdresseEnhet(rsRolle));
                enhet.setRegistreringsDato(localDateToXmlGregorianCalendar(rsRolle.getRegistreringsdato()));
                roller.getEnhet().add(enhet);
            }
        }
        return roller;
    }

    private static GrunndataUtskrift.Melding.RolleInnehaver mapTilRolleInnhaver(RolleoversiktTo to) {
        var rolleInnehaver = new GrunndataUtskrift.Melding.RolleInnehaver();
        rolleInnehaver.setNavn(mapTilNavntype(to.getNavn()));
        GrunndataUtskrift.Melding.RolleInnehaver.Fodselsdato fødselsdato = new GrunndataUtskrift.Melding.RolleInnehaver.Fodselsdato();
        fødselsdato.setValue(localDateToXmlGregorianCalendar(to.getFodselsdato()));
        rolleInnehaver.setFodselsdato(fødselsdato);
        rolleInnehaver.setAdresse(mapTilAdresse(to.getAdresse()));
        return rolleInnehaver;
    }

    private static GrunndataUtskrift.Melding.RolleInnehaver mapTilRolleInnhaver(RsRolleoversikt rsRolleoversikt) {
        var rolleInnehaver = new GrunndataUtskrift.Melding.RolleInnehaver();
        rolleInnehaver.setNavn(mapTilNavntype(rsRolleoversikt.getNavn()));
        GrunndataUtskrift.Melding.RolleInnehaver.Fodselsdato fødselsdato = new GrunndataUtskrift.Melding.RolleInnehaver.Fodselsdato();
        fødselsdato.setValue(localDateToXmlGregorianCalendar(rsRolleoversikt.getFodselsdato()));
        rolleInnehaver.setFodselsdato(fødselsdato);
        rolleInnehaver.setAdresse(mapTilAdresse(rsRolleoversikt.getAdresse()));
        return rolleInnehaver;
    }

    private static AdresseType1 mapTilAdresse(RsAdresse rsAdresse) {
        if (rsAdresse == null) {
            return null;
        }
        var adresse = new AdresseType1();
        adresse.setAdresse1(rsAdresse.getAdresse1());
        adresse.setAdresse2(rsAdresse.getAdresse2());
        adresse.setAdresse3(rsAdresse.getAdresse3());
        adresse.setPostnr(rsAdresse.getPostnr());
        adresse.setPoststed(rsAdresse.getPoststed());
        var land = new AdresseType1.Land();
        land.setLandkode1(rsAdresse.getLandKode());
        land.setValue(rsAdresse.getLandKode());
        adresse.setLand(land);
        return adresse;
    }

    private static AdresseType2 mapTilAdresse2(RsAdresse rsAdresse) {
        if (rsAdresse == null) {
            return null;
        }
        var adresse = new AdresseType2();
        adresse.setAdresse1(rsAdresse.getAdresse1());
        adresse.setAdresse2(rsAdresse.getAdresse2());
        adresse.setAdresse3(rsAdresse.getAdresse3());
        adresse.setPostnr(rsAdresse.getPostnr());
        adresse.setPoststed(rsAdresse.getPoststed());
        var kommune = new AdresseType2.Kommune();
        kommune.setValue(rsAdresse.getKommunenr());
        kommune.setKommnr(rsAdresse.getKommunenr());
        adresse.setKommune(kommune);
        var land = new AdresseType2.Land();
        land.setLandkode1(rsAdresse.getLandKode());
        land.setValue(rsAdresse.getLandKode());
        adresse.setLand(land);
        return adresse;
    }

    private static GrunndataUtskrift.Melding.Roller.Enhet.Adresse mapTilAdresseEnhet(RolleTo to) {
        var adresse = new GrunndataUtskrift.Melding.Roller.Enhet.Adresse();
        adresse.setForretningsAdresse(mapTilAdresse2(to.getForretningsAdresse()));
        adresse.setPostAdresse(mapTilAdresse2(to.getPostAdresse()));
        return adresse;
    }

    private static GrunndataUtskrift.Melding.Roller.Enhet.Adresse mapTilAdresseEnhet(RsRolle rsRolle) {
        var adresse = new GrunndataUtskrift.Melding.Roller.Enhet.Adresse();
        adresse.setForretningsAdresse(mapTilAdresse2(rsRolle.getForretningsAdresse()));
        adresse.setPostAdresse(mapTilAdresse2(rsRolle.getPostAdresse()));
        return adresse;
    }

    @SneakyThrows
    private static XMLGregorianCalendar localDateToXmlGregorianCalendar(LocalDate date) {
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(date.format(ISO_DATE));
    }

}
