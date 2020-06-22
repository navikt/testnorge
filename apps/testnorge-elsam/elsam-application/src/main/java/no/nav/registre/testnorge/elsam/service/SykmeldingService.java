package no.nav.registre.testnorge.elsam.service;

import static no.nav.registre.testnorge.elsam.utils.DatoUtil.hentAlderFraFnr;

import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import no.nav.helse.eiFellesformat.XMLMottakenhetBlokk;
import no.nav.helse.msgHead.*;
import no.nav.helse.eiFellesformat.XMLEIFellesformat;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.UUID;

import no.nav.registre.elsam.domain.Ident;
import no.nav.registre.elsam.domain.Organisasjon;
import no.nav.registre.elsam.domain.SykmeldingRequest;
import no.nav.registre.testnorge.elsam.domain.Sykemelding;
import no.nav.registre.testnorge.elsam.utils.ElsamXmlUtil;
import no.nav.registre.testnorge.elsam.utils.JAXB;

@Service
@Slf4j
public class SykmeldingService {

    private static final String SMTYPE = "SM2013";

    public Sykemelding opprettSykmelding(
            SykmeldingRequest sykmeldingRequest
    ) throws IOException {
        var resource = SykmeldingService.class.getResource("/fellesformat/sykmelding.xml");
        var iso88591 = Charsets.ISO_8859_1;
        byte[] bytes;

        InputStream inputStream = null;
        try {
            inputStream = resource.openStream();
            bytes = inputStream.readAllBytes();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        var sykmeldingXml = new String(bytes, iso88591);

        XMLEIFellesformat fellesformat = JAXB.unmarshalFellesformat(sykmeldingXml);

        var eid = UUID.randomUUID().toString();
        var fnr = sykmeldingRequest.getIdent().getFnr();
        var syketilfelleStartDato = sykmeldingRequest.getSyketilfelleStartDato();
        var utstedelsesdato = sykmeldingRequest.getUtstedelsesdato();
        var msgid = UUID.randomUUID().toString();
        var hovedDiagnose = sykmeldingRequest.getHovedDiagnose();
        var biDiagnoser = sykmeldingRequest.getBiDiagnoser();
        var legeFnr = sykmeldingRequest.getLege().getFnr();
        var manglendeTilrettelegging = sykmeldingRequest.getManglendeTilretteleggingPaaArbeidsplassen();
        var smtype = SMTYPE;
        var perioder = sykmeldingRequest.getSykmeldingPerioder();

        var ident = sykmeldingRequest.getIdent();
        var lege = sykmeldingRequest.getLege();
        var detaljer = sykmeldingRequest.getDetaljer();

        if (!SMTYPE.equals(smtype)) {
            throw new IllegalArgumentException("smtype " + smtype + " er ikke støttet.");
        }

        var iterator = fellesformat.getAny().iterator();

        XMLMsgHead xmlMsgHead = null;
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof XMLMsgHead) {
                xmlMsgHead = (XMLMsgHead) next;
                break;
            }
        }

        if (xmlMsgHead == null) {
            throw new ClassCastException("null cannot be cast to non-null type no.kith.xmlstds.msghead._2006_05_24.XMLMsgHead");
        }

        xmlMsgHead.getMsgInfo().setGenDate(LocalDateTime.now());

        opprettSender(xmlMsgHead.getMsgInfo().getSender().getOrganisation(), sykmeldingRequest);
        opprettMottaker(xmlMsgHead.getMsgInfo().getReceiver().getOrganisation(), sykmeldingRequest);

        var pasient = xmlMsgHead.getMsgInfo().getPatient();
        pasient.getIdent().forEach(identen -> identen.setId(fnr)); // TODO - kan ha flere ider/id-typer
        opprettPasient(pasient, ident);
        opprettAdresse(pasient.getAddress(), ident);

        var sykmelding = ElsamXmlUtil
                .lagHelseopplysninger(ident, syketilfelleStartDato, utstedelsesdato, lege, hovedDiagnose, biDiagnoser, perioder, smtype, manglendeTilrettelegging, detaljer);

        xmlMsgHead.getDocument().get(0).getRefDoc().getContent().getAny().set(0, sykmelding);
        xmlMsgHead.getMsgInfo().setMsgId(msgid);

        iterator = fellesformat.getAny().iterator();

        XMLMottakenhetBlokk xmlMottakenhetBlokk = null;
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof XMLMottakenhetBlokk) {
                xmlMottakenhetBlokk = (XMLMottakenhetBlokk) next;
                break;
            }
        }

        if (xmlMottakenhetBlokk == null) {
            throw new ClassCastException("null cannot be cast to non-null type no.trygdeetaten.xml.eiff._1.XMLMottakenhetBlokk");
        } else {
            xmlMottakenhetBlokk.setEdiLoggId(eid);
            xmlMottakenhetBlokk.setAvsenderFnrFraDigSignatur(legeFnr);
        }

        log.info("Opprettet sykmelding med eid {} og msgId {}", eid, msgid);

        return new Sykemelding(fellesformat);
    }

    private void opprettPasient(
            XMLPatient pasient,
            Ident ident
    ) {
        pasient.setGivenName(ident.getFornavn());
        pasient.setMiddleName(ident.getMellomnavn());
        pasient.setFamilyName(ident.getEtternavn());
        pasient.setDateOfBirth(hentAlderFraFnr(ident.getFnr()));
    }

    private void opprettAdresse(
            XMLAddress adresse,
            Ident ident
    ) {
        adresse.setStreetAdr(ident.getAdresse().getGate());
        adresse.setPostalCode(ident.getAdresse().getPostnummer());
        adresse.setCity(ident.getAdresse().getBy());
    }

    private void opprettSender(
            XMLOrganisation organisasjon,
            SykmeldingRequest sykmeldingRequest
    ) {
        opprettOrganisasjon(organisasjon, sykmeldingRequest.getSenderOrganisasjon());

        var healthcareProfessional = organisasjon.getHealthcareProfessional();
        healthcareProfessional.setGivenName(sykmeldingRequest.getLege().getFornavn());
        healthcareProfessional.setMiddleName(sykmeldingRequest.getLege().getMellomnavn());
        healthcareProfessional.setFamilyName(sykmeldingRequest.getLege().getEtternavn());
        var iterator = healthcareProfessional.getIdent().iterator();
        while (iterator.hasNext()) {
            XMLIdent ident = iterator.next();
            if ("HER".equals(ident.getTypeId().getV())) {
                if (sykmeldingRequest.getLege().getHerId() != null) {
                    ident.setId(sykmeldingRequest.getLege().getHerId());
                } else {
                    iterator.remove();
                }
            } else if ("FNR".equals(ident.getTypeId().getV())) {
                if (sykmeldingRequest.getLege().getFnr() != null) {
                    ident.setId(sykmeldingRequest.getLege().getFnr());
                } else {
                    iterator.remove();
                }
            } else if ("HPR".equals(ident.getTypeId().getV())) {
                if (sykmeldingRequest.getLege().getHprId() != null) {
                    ident.setId(sykmeldingRequest.getLege().getHprId());
                } else {
                    iterator.remove();
                }
            }
        }
        var teleAddress = healthcareProfessional.getTeleCom().get(0).getTeleAddress();
        teleAddress.setV("tel:" + sykmeldingRequest.getLege().getTelefon());
        healthcareProfessional.getTeleCom().forEach(tlf -> tlf.setTeleAddress(teleAddress));
    }

    private void opprettMottaker(
            XMLOrganisation xmlOrganisation,
            SykmeldingRequest sykmeldingRequest
    ) {
        opprettOrganisasjon(xmlOrganisation, sykmeldingRequest.getMottakerOrganisasjon());
    }

    private void opprettOrganisasjon(
            XMLOrganisation xmlOrganisation,
            Organisasjon organisasjon
    ) {
        xmlOrganisation.setOrganisationName(organisasjon.getNavn());
        var iterator = xmlOrganisation.getIdent().iterator();
        while (iterator.hasNext()) {
            XMLIdent ident = iterator.next();
            if ("HER".equals(ident.getTypeId().getV())) {
                if (organisasjon.getHerId() != null) {
                    ident.setId(organisasjon.getHerId());
                } else {
                    iterator.remove();
                }
            } else if ("ENH".equals(ident.getTypeId().getV())) {
                if (organisasjon.getOrgNr() != null) {
                    ident.setId(organisasjon.getOrgNr());
                } else {
                    iterator.remove();
                }
            }
        }
        var address = xmlOrganisation.getAddress();
        address.setStreetAdr(organisasjon.getAdresse().getGate());
        address.setPostalCode(organisasjon.getAdresse().getPostnummer());
        address.setCity(organisasjon.getAdresse().getBy());
    }
}
