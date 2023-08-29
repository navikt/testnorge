package no.nav.registre.testnorge.sykemelding.domain;

import lombok.SneakyThrows;
import lombok.ToString;
import no.nav.registre.testnorge.sykemelding.external.eiFellesformat.XMLEIFellesformat;
import no.nav.registre.testnorge.sykemelding.external.eiFellesformat.XMLMottakenhetBlokk;
import no.nav.registre.testnorge.sykemelding.external.msgHead.XMLAddress;
import no.nav.registre.testnorge.sykemelding.external.msgHead.XMLCS;
import no.nav.registre.testnorge.sykemelding.external.msgHead.XMLHealthcareProfessional;
import no.nav.registre.testnorge.sykemelding.external.msgHead.XMLIdent;
import no.nav.registre.testnorge.sykemelding.external.msgHead.XMLMsgHead;
import no.nav.registre.testnorge.sykemelding.external.msgHead.XMLOrganisation;
import no.nav.registre.testnorge.sykemelding.external.msgHead.XMLPatient;
import no.nav.registre.testnorge.sykemelding.util.JAXBSykemeldingConverter;
import no.nav.registre.testnorge.sykemelding.util.StaticResourceLoader;
import no.nav.testnav.libs.dto.sykemelding.v1.AdresseDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.HelsepersonellDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.OrganisasjonDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.PasientDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldingDTO;

import javax.xml.datatype.DatatypeFactory;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.nonNull;

@ToString
public class Sykemelding {
    private final XMLEIFellesformat fellesformat;
    private final LocalDate fom;
    private final LocalDate tom;
    private final String ident;

    @SneakyThrows
    public Sykemelding(SykemeldingDTO dto, ApplicationInfo applicationInfo) {
        var xml = StaticResourceLoader.loadAsString("sykmelding.xml", StandardCharsets.ISO_8859_1);
        fellesformat = JAXBSykemeldingConverter.getInstance().convertToXMLEIFellesformat(xml);
        var head = getXMLMsgHead();
        head.getMsgInfo().setGenDate(LocalDateTime.now());
        head.getMsgInfo().setMsgId(UUID.randomUUID().toString());

        updateOrganisation(head.getMsgInfo().getSender().getOrganisation(), dto.getSender(), dto.getHelsepersonell());
        updateOrganisation(head.getMsgInfo().getReceiver().getOrganisation(), dto.getMottaker());
        updatePatient(head.getMsgInfo().getPatient(), dto.getPasient());
        var dokument = new Dokument(dto, applicationInfo);
        head.getDocument().get(0).getRefDoc().getContent().getAny().set(0, dokument.getXmlObject());

        fom = dokument.getFom();
        tom = dokument.getTom();
        ident = dto.getPasient().getIdent();

        var xmlMottakenhetBlokk = getXMLMottakenhetBlokk();
        xmlMottakenhetBlokk.setEdiLoggId(UUID.randomUUID().toString());
        xmlMottakenhetBlokk.setAvsenderFnrFraDigSignatur(dto.getHelsepersonell().getIdent());
        xmlMottakenhetBlokk.setMottattDatotid(
                DatatypeFactory.newInstance().newXMLGregorianCalendar(
                        GregorianCalendar.from(dto.getStartDato().atStartOfDay(ZoneId.systemDefault()))
                )
        );
    }

    public LocalDate getFom() {
        return fom;
    }

    public LocalDate getTom() {
        return tom;
    }

    public String getIdent() {
        return ident;
    }

    public String toXml() {
        return JAXBSykemeldingConverter.getInstance().convertToXml(fellesformat);
    }

    public String getMsgId() {
        return getXMLMsgHead().getMsgInfo().getMsgId();
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
            XMLAddress address = patient.getAddress() == null ? new XMLAddress() : patient.getAddress();
            address.setStreetAdr(adresse.getGate());
            address.setPostalCode(adresse.getPostnummer());
            address.setCity(adresse.getBy());
        }
    }

    private void updateOrganisation(XMLOrganisation organisation, OrganisasjonDTO dto, HelsepersonellDTO helsepersonellDTO) {
        organisation.setOrganisationName(nonNull(dto) ? dto.getNavn() : null);

        if (nonNull(helsepersonellDTO)) {
            XMLHealthcareProfessional healthcareProfessional = organisation.getHealthcareProfessional();
            healthcareProfessional.setFamilyName(helsepersonellDTO.getEtternavn());
            healthcareProfessional.setGivenName(helsepersonellDTO.getFornavn());
            healthcareProfessional.setMiddleName(helsepersonellDTO.getMellomnavn());
            XMLIdent hpr = getXMLIdent(healthcareProfessional.getIdent(), "HPR");
            hpr.setId(helsepersonellDTO.getHprId());
            XMLIdent fnr = getXMLIdent(healthcareProfessional.getIdent(), "FNR");
            fnr.setId(helsepersonellDTO.getIdent());
            organisation.setHealthcareProfessional(healthcareProfessional);
        }

        XMLAddress xmlAddress = new XMLAddress();
        XMLCS adresseType = new XMLCS();
        adresseType.setDN("Postadresse");
        adresseType.setV("PST");
        xmlAddress.setType(adresseType);
        xmlAddress.setCity(nonNull(dto) ? dto.getAdresse().getBy() : null);
        xmlAddress.setPostalCode(nonNull(dto) ? dto.getAdresse().getPostnummer() : null);
        xmlAddress.setStreetAdr(nonNull(dto) ? dto.getAdresse().getGate() : null);
        organisation.setAddress(xmlAddress);
        XMLIdent enhIdent = getXMLIdent(organisation.getIdent(), "ENH");
        enhIdent.setId(nonNull(dto) ? dto.getOrgNr() : null);
    }

    private void updateOrganisation(XMLOrganisation organisation, OrganisasjonDTO dto) {
        updateOrganisation(organisation, dto, null);
    }

    private XMLIdent getXMLIdent(List<XMLIdent> list, String type) {
        return list.stream()
                .filter(value -> value.getTypeId().getV().equals(type))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Klarte ikke å finne ident av type " + type));
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
}
