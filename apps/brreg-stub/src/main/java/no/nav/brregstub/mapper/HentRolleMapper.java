package no.nav.brregstub.mapper;

import lombok.SneakyThrows;
import no.nav.brregstub.api.common.RolleKode;
import no.nav.brregstub.api.common.RsOrganisasjon;
import no.nav.brregstub.api.common.RsPersonOgRolle;
import no.nav.brregstub.api.common.RsSamendring;
import no.nav.brregstub.generated.Grunndata;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static no.nav.brregstub.api.common.UnderstatusKode.understatusKoder;

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

    private static Grunndata.ResponseHeader mapTilResponseHeader(RsOrganisasjon rsOrganisasjon) {
        var responseHeader = new Grunndata.ResponseHeader();
        responseHeader.setProssessDato(localDateToXmlGregorianCalendar(LocalDate.now()));
        responseHeader.setTjeneste(TJENESTE_NAVN);
        responseHeader.setOrgnr(rsOrganisasjon.getOrgnr());
        responseHeader.setHovedStatus(rsOrganisasjon.getHovedstatus() == null ? 0 : rsOrganisasjon.getHovedstatus());
        var underStatus = new Grunndata.ResponseHeader.UnderStatus();
        for (Integer understatus : rsOrganisasjon.getUnderstatuser()) {
            var underStatusMelding = new Grunndata.ResponseHeader.UnderStatus.UnderStatusMelding();
            underStatusMelding.setKode(understatus);
            underStatusMelding.setValue(understatusKoder.get(understatus));

            underStatus.getUnderStatusMelding().add(underStatusMelding);
        }
        responseHeader.setUnderStatus(underStatus);
        return responseHeader;
    }

    private static Grunndata.Melding mapTilMelding(RsOrganisasjon rsOrganisasjon) {
        var melding = new Grunndata.Melding();
        melding.setOrganisasjonsnummer(mapTilOrganisasjonsNummer(rsOrganisasjon));
        melding.setKontaktperson(mapTilKontaktperson(rsOrganisasjon));
        melding.setStyre(mapTilStyre(rsOrganisasjon));
        melding.setDeltakere(mapTilDeltakere(rsOrganisasjon));
        melding.setKomplementar(mapTilKomplementar(rsOrganisasjon));
        melding.setSameiere(mapTilSameiere(rsOrganisasjon));
        melding.setTjeneste(TJENESTE_NAVN);

        if (melding.getKontaktperson() == null) {
            melding.setKontaktperson(new Grunndata.Melding.Kontaktperson());
        }
        if (melding.getStyre() == null) {
            melding.setStyre(new Grunndata.Melding.Styre());
        }
        if (melding.getDeltakere() == null) {
            melding.setDeltakere(new Grunndata.Melding.Deltakere());
        }
        if (melding.getKomplementar() == null) {
            melding.setKomplementar(new Grunndata.Melding.Komplementar());
        }
        if (melding.getSameiere() == null) {
            melding.setSameiere(new Grunndata.Melding.Sameiere());
        }

        return melding;
    }

    private static Grunndata.Melding.Kontaktperson mapTilKontaktperson(RsOrganisasjon rsOrganisasjon) {
        var kontaktperson = new Grunndata.Melding.Kontaktperson();
        if (rsOrganisasjon.getKontaktperson() != null) {
            kontaktperson.getSamendring().add(mapTilSamendring(rsOrganisasjon.getKontaktperson(), RolleKode.KONT));
        }
        return kontaktperson;
    }

    private static Grunndata.Melding.Styre mapTilStyre(RsOrganisasjon rsOrganisasjon) {
        var styre = new Grunndata.Melding.Styre();
        if (rsOrganisasjon.getStyre() != null) {
            styre.getSamendring().add(mapTilSamendring(rsOrganisasjon.getStyre(), RolleKode.STYR));
        }
        return styre;
    }

    private static Grunndata.Melding.Deltakere mapTilDeltakere(RsOrganisasjon rsOrganisasjon) {
        var deltakere = new Grunndata.Melding.Deltakere();
        if (rsOrganisasjon.getDeltakere() != null) {
            deltakere.getSamendring().add(mapTilSamendring(rsOrganisasjon.getDeltakere(), RolleKode.DELT));
        }
        return deltakere;
    }

    private static Grunndata.Melding.Komplementar mapTilKomplementar(RsOrganisasjon rsOrganisasjon) {
        var komplementar = new Grunndata.Melding.Komplementar();
        if (rsOrganisasjon.getKomplementar() != null) {
            komplementar.getSamendring().add(mapTilSamendring(rsOrganisasjon.getKomplementar(), RolleKode.KOMP));
        }
        return komplementar;
    }

    private static Grunndata.Melding.Sameiere mapTilSameiere(RsOrganisasjon rsOrganisasjon) {
        var sameier = new Grunndata.Melding.Sameiere();
        if (rsOrganisasjon.getSameier() != null) {
            sameier.getSamendring().add(mapTilSamendring(rsOrganisasjon.getSameier(), RolleKode.SAM));
        }
        return sameier;
    }

    private static Grunndata.Melding.Eierkommune.Samendring mapTilSamendring(
            RsSamendring rsSamendring,
            RolleKode rolleKode
    ) {
        if (!rsSamendring.getRoller().isEmpty()) {
            var samendring = new Grunndata.Melding.Eierkommune.Samendring();
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

    private static Grunndata.Melding.Eierkommune.Samendring.Rolle mapTilSamendringRolle(RsPersonOgRolle rsPersonOgRolle) {
        var rolle = new Grunndata.Melding.Eierkommune.Samendring.Rolle();
        rolle.setBeskrivelse(rsPersonOgRolle.getRollebeskrivelse());
        rolle.setRolletype(rsPersonOgRolle.getRolle());

        var person = new Grunndata.Melding.Eierkommune.Samendring.Rolle.Person();
        person.setBeskrivelse("Lever");
        person.setStatuskode("L");
        person.setFodselsnr(rsPersonOgRolle.getFodselsnr());
        person.setFornavn(rsPersonOgRolle.getFornavn());
        person.setSlektsnavn(rsPersonOgRolle.getSlektsnavn());
        person.setAdresse1(rsPersonOgRolle.getAdresse1());
        person.setFratraadt(rsPersonOgRolle.isFratraadt() ? "F" : "N");
        person.setPoststed(rsPersonOgRolle.getPoststed());
        person.setPostnr((rsPersonOgRolle.getPostnr()));
        var land = new Grunndata.Melding.Eierkommune.Samendring.Rolle.Person.Land();
        land.setLandkode4("NOR");
        land.setValue("Norge");
        person.setLand(land);
        rolle.getPerson().add(person);
        return rolle;
    }

    private static Grunndata.Melding.Organisasjonsnummer mapTilOrganisasjonsNummer(RsOrganisasjon rsOrganisasjon) {
        var orgnr = new Grunndata.Melding.Organisasjonsnummer();
        orgnr.setRegistreringsDato(localDateToXmlGregorianCalendar(rsOrganisasjon.getRegistreringsdato()));
        orgnr.setValue(String.valueOf(rsOrganisasjon.getOrgnr()));
        return orgnr;
    }

    @SneakyThrows
    private static XMLGregorianCalendar localDateToXmlGregorianCalendar(LocalDate date) {
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(date.format(ISO_DATE));
    }

}
