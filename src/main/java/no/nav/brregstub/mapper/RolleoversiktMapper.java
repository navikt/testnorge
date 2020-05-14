package no.nav.brregstub.mapper;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static no.nav.brregstub.api.common.UnderstatusKode.understatusKoder;

import lombok.SneakyThrows;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;

import no.nav.brregstub.api.common.RolleKode;
import no.nav.brregstub.api.common.RsAdresse;
import no.nav.brregstub.api.common.RsNavn;
import no.nav.brregstub.api.v1.RolleTo;
import no.nav.brregstub.api.v1.RolleoversiktTo;
import no.nav.brregstub.api.v2.RsRolle;
import no.nav.brregstub.api.v2.RsRolleoversikt;
import no.nav.brregstub.tjenestekontrakter.rolleutskrift.AdresseType1;
import no.nav.brregstub.tjenestekontrakter.rolleutskrift.AdresseType2;
import no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata;
import no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata.Melding;
import no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata.Melding.RolleInnehaver;
import no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata.Melding.RolleInnehaver.Fodselsdato;
import no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata.Melding.Roller.Enhet;
import no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata.ResponseHeader;
import no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata.ResponseHeader.UnderStatus;
import no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata.ResponseHeader.UnderStatus.UnderStatusMelding;
import no.nav.brregstub.tjenestekontrakter.rolleutskrift.NavnType;

public class RolleoversiktMapper {

    public static final String TJENESTE_NAVN = "hentRolleutskrift";

    public static Grunndata map(RolleoversiktTo to) {
        var grunndata = new Grunndata();
        var responseHeader = mapTilResponseHeader(to);
        grunndata.setResponseHeader(responseHeader);

        if (to.getHovedstatus() == 0) {
            var melding = mapTilMelding(to);
            grunndata.setMelding(melding);
        }

        return grunndata;
    }

    public static Grunndata map(RsRolleoversikt rsRolleoversikt) {
        var grunndata = new Grunndata();
        var responseHeader = mapTilResponseHeader(rsRolleoversikt);
        grunndata.setResponseHeader(responseHeader);

        if (rsRolleoversikt.getHovedstatus() == 0) {
            var melding = mapTilMelding(rsRolleoversikt);
            grunndata.setMelding(melding);
        }

        return grunndata;
    }

    private static ResponseHeader mapTilResponseHeader(RolleoversiktTo to) {
        var responseHeader = new ResponseHeader();
        responseHeader.setProssessDato(localDateToXmlGregorianCalendar(LocalDate.now()));
        responseHeader.setTjeneste(TJENESTE_NAVN);
        responseHeader.setFodselsnr(to.getFnr());
        responseHeader.setHovedStatus(to.getHovedstatus());
        var underStatus = new UnderStatus();
        if (to.getUnderstatuser().isEmpty()) {
            var underStatusMelding = new UnderStatusMelding();
            underStatusMelding.setKode(0);
            underStatusMelding.setValue(understatusKoder.get(0));

            underStatus.getUnderStatusMelding().add(underStatusMelding);
        } else {
            to.getUnderstatuser().forEach(understatus -> {
                var underStatusMelding = new UnderStatusMelding();
                underStatusMelding.setKode(understatus);
                underStatusMelding.setValue(understatusKoder.get(understatus));
                underStatus.getUnderStatusMelding().add(underStatusMelding);
            });
        }

        responseHeader.setUnderStatus(underStatus);
        return responseHeader;
    }

    private static ResponseHeader mapTilResponseHeader(RsRolleoversikt rsRolleoversikt) {
        var responseHeader = new ResponseHeader();
        responseHeader.setProssessDato(localDateToXmlGregorianCalendar(LocalDate.now()));
        responseHeader.setTjeneste(TJENESTE_NAVN);
        responseHeader.setFodselsnr(rsRolleoversikt.getFnr());
        responseHeader.setHovedStatus(rsRolleoversikt.getHovedstatus());
        var underStatus = new UnderStatus();
        if (rsRolleoversikt.getUnderstatuser().isEmpty()) {
            var underStatusMelding = new UnderStatusMelding();
            underStatusMelding.setKode(0);
            underStatusMelding.setValue(understatusKoder.get(0));

            underStatus.getUnderStatusMelding().add(underStatusMelding);
        } else {
            rsRolleoversikt.getUnderstatuser().forEach(understatus -> {
                var underStatusMelding = new UnderStatusMelding();
                underStatusMelding.setKode(understatus);
                underStatusMelding.setValue(understatusKoder.get(understatus));
                underStatus.getUnderStatusMelding().add(underStatusMelding);
            });
        }

        responseHeader.setUnderStatus(underStatus);
        return responseHeader;
    }

    private static Melding mapTilMelding(RolleoversiktTo to) {
        var melding = new Melding();
        melding.setRolleInnehaver(mapTilRolleInnhaver(to));
        melding.setRoller(mapTilRoller(to));
        melding.setTjeneste(TJENESTE_NAVN);

        return melding;
    }

    private static Melding mapTilMelding(RsRolleoversikt rsRolleoversikt) {
        var melding = new Melding();
        melding.setRolleInnehaver(mapTilRolleInnhaver(rsRolleoversikt));
        melding.setRoller(mapTilRoller(rsRolleoversikt));
        melding.setTjeneste(TJENESTE_NAVN);

        return melding;
    }

    private static Melding.Roller mapTilRoller(RolleoversiktTo to) {
        var roller = new Melding.Roller();
        if (to.getEnheter() != null) {
            int count = 1;
            for (RolleTo enhetTo : to.getEnheter()) {
                var enhet = new Enhet();
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

    private static Melding.Roller mapTilRoller(RsRolleoversikt rsRolleoversikt) {
        var roller = new Melding.Roller();
        if (rsRolleoversikt.getEnheter() != null) {
            int count = 1;
            for (RsRolle rsRolle : rsRolleoversikt.getEnheter()) {
                var enhet = new Enhet();
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

    public static Enhet.RolleBeskrivelse mapTilRollebeskrivelse(String beskrivelseTo) {
        var beskrivelse = new Enhet.RolleBeskrivelse();
        beskrivelse.setValue(beskrivelseTo);
        beskrivelse.setLedetekst("Rolle");
        return beskrivelse;
    }

    public static Enhet.RolleBeskrivelse mapTilRollebeskrivelse(RolleKode rolleKode) {
        var beskrivelse = new Enhet.RolleBeskrivelse();
        beskrivelse.setValue(rolleKode.getBeskrivelse());
        beskrivelse.setLedetekst("Rolle");
        return beskrivelse;
    }

    public static Enhet.Orgnr mapTilOrganisasjonsNummer(Integer orgnrTo) {
        var orgnr = new Enhet.Orgnr();
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

    private static RolleInnehaver mapTilRolleInnhaver(RolleoversiktTo to) {
        var rolleInnehaver = new RolleInnehaver();
        rolleInnehaver.setNavn(mapTilNavntype(to.getNavn()));
        Fodselsdato fødselsdato = new Fodselsdato();
        fødselsdato.setValue(localDateToXmlGregorianCalendar(to.getFodselsdato()));
        rolleInnehaver.setFodselsdato(fødselsdato);
        rolleInnehaver.setAdresse(mapTilAdresse(to.getAdresse()));
        return rolleInnehaver;
    }

    private static RolleInnehaver mapTilRolleInnhaver(RsRolleoversikt rsRolleoversikt) {
        var rolleInnehaver = new RolleInnehaver();
        rolleInnehaver.setNavn(mapTilNavntype(rsRolleoversikt.getNavn()));
        Fodselsdato fødselsdato = new Fodselsdato();
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

    private static Enhet.Adresse mapTilAdresseEnhet(RolleTo to) {
        var adresse = new Enhet.Adresse();
        adresse.setForretningsAdresse(mapTilAdresse2(to.getForretningsAdresse()));
        adresse.setPostAdresse(mapTilAdresse2(to.getPostAdresse()));
        return adresse;
    }

    private static Enhet.Adresse mapTilAdresseEnhet(RsRolle rsRolle) {
        var adresse = new Enhet.Adresse();
        adresse.setForretningsAdresse(mapTilAdresse2(rsRolle.getForretningsAdresse()));
        adresse.setPostAdresse(mapTilAdresse2(rsRolle.getPostAdresse()));
        return adresse;
    }

    @SneakyThrows
    private static XMLGregorianCalendar localDateToXmlGregorianCalendar(LocalDate date) {
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(date.format(ISO_DATE));
    }

}
