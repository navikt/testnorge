package no.nav.registre.testnorge.sykemelding.domain;

import lombok.SneakyThrows;

import javax.xml.datatype.DatatypeFactory;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

import no.nav.helse.eiFellesformat.XMLEIFellesformat;
import no.nav.helse.eiFellesformat.XMLMottakenhetBlokk;
import no.nav.helse.msgHead.XMLAddress;
import no.nav.helse.msgHead.XMLIdent;
import no.nav.helse.msgHead.XMLMsgHead;
import no.nav.helse.msgHead.XMLOrganisation;
import no.nav.helse.msgHead.XMLPatient;
import no.nav.registre.testnorge.dto.sykemelding.v1.AdresseDTO;
import no.nav.registre.testnorge.dto.sykemelding.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.dto.sykemelding.v1.PasientDTO;
import no.nav.registre.testnorge.dto.sykemelding.v1.SykemeldingDTO;
import no.nav.registre.testnorge.sykemelding.util.JAXBSykemeldingConverter;
import no.nav.registre.testnorge.sykemelding.util.StaticResourceLoader;

public class Sykemelding {
    private final XMLEIFellesformat fellesformat;

    @SneakyThrows
    public Sykemelding(SykemeldingDTO dto, ApplicationInfo applicationInfo) {
        var xml = StaticResourceLoader.loadAsString("sykmelding.xml", StandardCharsets.ISO_8859_1);
        fellesformat = JAXBSykemeldingConverter.getInstance().convertToXMLEIFellesformat(xml);
        var head = getXMLMsgHead();
        head.getMsgInfo().setGenDate(LocalDateTime.now());
        head.getMsgInfo().setMsgId(UUID.randomUUID().toString());

        updateOrganisation(head.getMsgInfo().getSender().getOrganisation(), dto.getSender());
        updateOrganisation(head.getMsgInfo().getReceiver().getOrganisation(), dto.getMottaker());
        updatePatient(head.getMsgInfo().getPatient(), dto.getPasient());
        var dokument = new Dokument(dto, applicationInfo);
        head.getDocument().get(0).getRefDoc().getContent().getAny().set(0, dokument.getXmlObject());

        var xmlMottakenhetBlokk = getXMLMottakenhetBlokk();
        xmlMottakenhetBlokk.setEdiLoggId(UUID.randomUUID().toString());
        xmlMottakenhetBlokk.setAvsenderFnrFraDigSignatur(dto.getLege().getIdent());
        xmlMottakenhetBlokk.setMottattDatotid(
                DatatypeFactory.newInstance().newXMLGregorianCalendar(dto.getStartDato().toString())
        );
    }

    private XMLMsgHead getXMLMsgHead() {
        return findObjectFromClass(value -> value instanceof XMLMsgHead);
    }


    private void updatePatient(XMLPatient patient, PasientDTO dto) {
        patient.getIdent().forEach(value -> value.setId(dto.getIdent()));
        patient.setGivenName(dto.getFornavn());
        patient.setMiddleName(dto.getMellomnavn());
        patient.setFamilyName(dto.getEtternavn());
        patient.setDateOfBirth(dto.getFoedselsdato());

        AdresseDTO adresse = dto.getAdresse();
        if (adresse != null) {
            XMLAddress address = patient.getAddress();
            address.setStreetAdr(adresse.getGate());
            address.setPostalCode(adresse.getPostnummer());
            address.setCity(adresse.getBy());
        }
    }


    private void updateOrganisation(XMLOrganisation organisation, OrganisasjonDTO dto) {
        organisation.setOrganisationName(dto.getNavn());
        XMLIdent enhIdent = organisation
                .getIdent()
                .stream()
                .filter(value -> value.getTypeId().getV().equals("ENH"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Klarte ikke å finne ident av type ENH for organisasjon"));
        enhIdent.setId(dto.getOrgNr());
    }

    @SuppressWarnings("unchecked")
    private <T> T findObjectFromClass(final FilterInstanceOf filterAction) {
        return fellesformat
                .getAny()
                .stream()
                .filter(filterAction::filter)
                .map(value -> (T) value)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("klarer ikke å finne xml element i XMLEIFellesformat"));
    }

    private XMLMottakenhetBlokk getXMLMottakenhetBlokk() {
        return findObjectFromClass(value -> value instanceof XMLMottakenhetBlokk);
    }


    @FunctionalInterface
    private interface FilterInstanceOf {
        boolean filter(Object value);
    }


    public String toXml() {
        return JAXBSykemeldingConverter.getInstance().convertToXml(fellesformat);
    }

    public String getMsgId() {
        return getXMLMsgHead().getMsgInfo().getMsgId();
    }
}
