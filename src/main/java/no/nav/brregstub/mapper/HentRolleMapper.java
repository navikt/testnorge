package no.nav.brregstub.mapper;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static no.nav.brregstub.api.common.UnderstatusKode.understatusKoder;

import lombok.SneakyThrows;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;

import no.nav.brregstub.api.common.RolleKode;
import no.nav.brregstub.api.common.RsOrganisasjon;
import no.nav.brregstub.api.common.RsPersonOgRolle;
import no.nav.brregstub.api.common.RsSamendring;
import no.nav.brregstub.tjenestekontrakter.hentroller.Grunndata;
import no.nav.brregstub.tjenestekontrakter.hentroller.Grunndata.Melding;
import no.nav.brregstub.tjenestekontrakter.hentroller.Grunndata.Melding.Eierkommune.Samendring;
import no.nav.brregstub.tjenestekontrakter.hentroller.Grunndata.ResponseHeader;
import no.nav.brregstub.tjenestekontrakter.hentroller.Grunndata.ResponseHeader.UnderStatus.UnderStatusMelding;

public class HentRolleMapper {

    public static final String TJENESTE_NAVN = "hentRoller";

    public static Grunndata map(RsOrganisasjon rsOrganisasjon) {
        var grunndata = new Grunndata();
        var responseHeader = mapTilResponseHeader(rsOrganisasjon);
        grunndata.setResponseHeader(responseHeader);

        if (rsOrganisasjon.getHovedstatus() == 0) {
            var melding = mapTilMelding(rsOrganisasjon);
            grunndata.setMelding(melding);
        }
        return grunndata;
    }

    private static ResponseHeader mapTilResponseHeader(RsOrganisasjon rsOrganisasjon) {
        var responseHeader = new ResponseHeader();
        responseHeader.setProssessDato(localDateToXmlGregorianCalendar(LocalDate.now()));
        responseHeader.setTjeneste(TJENESTE_NAVN);
        responseHeader.setOrgnr(rsOrganisasjon.getOrgnr());
        responseHeader.setHovedStatus(rsOrganisasjon.getHovedstatus() == null ? 0 : rsOrganisasjon.getHovedstatus());
        var underStatus = new ResponseHeader.UnderStatus();
        for (Integer understatus : rsOrganisasjon.getUnderstatuser()) {
            var underStatusMelding = new UnderStatusMelding();
            underStatusMelding.setKode(understatus);
            underStatusMelding.setValue(understatusKoder.get(understatus));

            underStatus.getUnderStatusMelding().add(underStatusMelding);
        }
        responseHeader.setUnderStatus(underStatus);
        return responseHeader;
    }

    private static Melding mapTilMelding(RsOrganisasjon rsOrganisasjon) {
        var melding = new Melding();
        melding.setOrganisasjonsnummer(mapTilOrganisasjonsNummer(rsOrganisasjon));
        melding.setKontaktperson(mapTilKontaktperson(rsOrganisasjon));
        melding.setStyre(mapTilStyre(rsOrganisasjon));
        melding.setDeltakere(mapTilDeltakere(rsOrganisasjon));
        melding.setKomplementar(mapTilKomplementar(rsOrganisasjon));
        melding.setSameiere(mapTilSameiere(rsOrganisasjon));
        melding.setTjeneste(TJENESTE_NAVN);

        return melding;
    }

    private static Melding.Kontaktperson mapTilKontaktperson(RsOrganisasjon rsOrganisasjon) {
        if (rsOrganisasjon.getKontaktperson() != null) {
            var kontaktperson = new Melding.Kontaktperson();
            kontaktperson.getSamendring().add(mapTilSamendring(rsOrganisasjon.getKontaktperson(), RolleKode.KONT));
            return kontaktperson;
        }
        return null;
    }

    private static Melding.Styre mapTilStyre(RsOrganisasjon rsOrganisasjon) {
        if (rsOrganisasjon.getStyre() != null) {
            var styre = new Melding.Styre();
            styre.getSamendring().add(mapTilSamendring(rsOrganisasjon.getStyre(), RolleKode.STYR));
            return styre;
        }
        return null;
    }

    private static Melding.Deltakere mapTilDeltakere(RsOrganisasjon rsOrganisasjon) {
        if (rsOrganisasjon.getDeltakere() != null) {
            var deltakere = new Melding.Deltakere();
            deltakere.getSamendring().add(mapTilSamendring(rsOrganisasjon.getDeltakere(), RolleKode.DELT));
            return deltakere;
        }
        return null;
    }

    private static Melding.Komplementar mapTilKomplementar(RsOrganisasjon rsOrganisasjon) {
        if (rsOrganisasjon.getKomplementar() != null) {
            var komplementar = new Melding.Komplementar();
            komplementar.getSamendring().add(mapTilSamendring(rsOrganisasjon.getKomplementar(), RolleKode.KOMP));
            return komplementar;
        }
        return null;
    }

    private static Melding.Sameiere mapTilSameiere(RsOrganisasjon rsOrganisasjon) {
        if (rsOrganisasjon.getSameier() != null) {
            var sameier = new Melding.Sameiere();
            sameier.getSamendring().add(mapTilSamendring(rsOrganisasjon.getSameier(), RolleKode.SAM));
            return sameier;
        }
        return null;
    }

    private static Samendring mapTilSamendring(
            RsSamendring rsSamendring,
            RolleKode rolleKode
    ) {
        if (rsSamendring.getRoller().size() > 0) {
            var samendring = new Samendring();
            samendring.setSamendringstype(rolleKode.name());
            samendring.setBeskrivelse(rolleKode.getBeskrivelse());
            samendring.setRegistreringsDato(localDateToXmlGregorianCalendar(rsSamendring.getRegistringsDato()));

            for (var rolle : rsSamendring.getRoller()) {
                samendring.getRolle().add(mapTilSamendringRolle(rolle));
            }

            return samendring;
        }
        return null;
    }

    private static Samendring.Rolle mapTilSamendringRolle(RsPersonOgRolle rsPersonOgRolle) {
        var rolle = new Samendring.Rolle();
        rolle.setBeskrivelse(rsPersonOgRolle.getRollebeskrivelse());
        rolle.setRolletype(rsPersonOgRolle.getRolle());

        var person = new Samendring.Rolle.Person();
        person.setBeskrivelse("Lever");
        person.setStatuskode("L");
        person.setFodselsnr(rsPersonOgRolle.getFodselsnr());
        person.setFornavn(rsPersonOgRolle.getFornavn());
        person.setSlektsnavn(rsPersonOgRolle.getSlektsnavn());
        person.setAdresse1(rsPersonOgRolle.getAdresse1());
        person.setFratraadt(rsPersonOgRolle.isFratraadt() ? "F" : "N");
        person.setPoststed(rsPersonOgRolle.getPoststed());
        person.setPostnr((rsPersonOgRolle.getPostnr()));
        var land = new Samendring.Rolle.Person.Land();
        land.setLandkode4("NOR");
        land.setValue("Norge");
        person.setLand(land);
        rolle.getPerson().add(person);
        return rolle;
    }

    private static Melding.Organisasjonsnummer mapTilOrganisasjonsNummer(RsOrganisasjon rsOrganisasjon) {
        var orgnr = new Melding.Organisasjonsnummer();
        orgnr.setRegistreringsDato(localDateToXmlGregorianCalendar(rsOrganisasjon.getRegistreringsdato()));
        orgnr.setValue(String.valueOf(rsOrganisasjon.getOrgnr()));
        return orgnr;
    }

    @SneakyThrows
    private static XMLGregorianCalendar localDateToXmlGregorianCalendar(LocalDate date) {
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(date.format(ISO_DATE));
    }

}
