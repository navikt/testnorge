package no.nav.registre.inntektsmeldingstub.provider;

import lombok.RequiredArgsConstructor;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLInntektsmeldingM;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.List;

import no.nav.registre.inntektsmeldingstub.MeldingsType;
import no.nav.registre.inntektsmeldingstub.database.model.Inntektsmelding;
import no.nav.registre.inntektsmeldingstub.provider.validation.InntektsmeldingRequestValidator;
import no.nav.registre.inntektsmeldingstub.provider.validation.ValidationException;
import no.nav.registre.inntektsmeldingstub.service.DBToRestMapper;
import no.nav.registre.inntektsmeldingstub.service.InntektsmeldingService;
import no.nav.registre.inntektsmeldingstub.service.rs.RsInntektsmelding;
import no.nav.registre.inntektsmeldingstub.util.XmlInntektsmelding201809;
import no.nav.registre.inntektsmeldingstub.util.XmlInntektsmelding201812;

@RestController
@RequestMapping("/api/v1/inntektsmelding")
@RequiredArgsConstructor
public class InntektsmeldingController {

    private final InntektsmeldingService service;

    @GetMapping(value = "/2018/09/xml/{id}", produces = "application/xml")
    public XMLInntektsmeldingM hentInntektsmelding201809(
            @PathVariable Long id
    ) {
        return XmlInntektsmelding201809.createInntektsmelding(DBToRestMapper.mapDBMelding(service.findInntektsmelding(id)));
    }

    @GetMapping("/2018/09/json/{id}")
    public ResponseEntity<RsInntektsmelding> hentInntektsmeldingJSON201809(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(DBToRestMapper.mapDBMelding(service.findInntektsmelding(id)));
    }

    @PostMapping("/2018/09")
    public ResponseEntity<List<Inntektsmelding>> opprettInntektsmeldinger201809(
            @RequestParam String eier,
            @RequestBody List<RsInntektsmelding> meldinger
    ) {
        try {
            InntektsmeldingRequestValidator.validate(meldinger, MeldingsType.TYPE_2018_09, eier);
        } catch (ValidationException e) {
            String errorstring = "Kunne ikke opprette inntektsmelding:\n" + e.getErrors();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorstring);
        }

        return ResponseEntity.ok(
                service.saveMeldinger(meldinger, MeldingsType.TYPE_2018_09, eier)
        );
    }

    @GetMapping(value = "/2018/12/xml/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public no.seres.xsd.nav.inntektsmelding_m._20181211.XMLInntektsmeldingM hentInntektsmelding201812(@PathVariable Long id) {
        return XmlInntektsmelding201812.createInntektsmelding(DBToRestMapper.mapDBMelding(service.findInntektsmelding(id)));
    }

    @GetMapping("/2018/12/json/{id}")
    public ResponseEntity<RsInntektsmelding> hentInntektsmeldingJSON201812(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(DBToRestMapper.mapDBMelding(service.findInntektsmelding(id)));
    }

    @PostMapping(value = "/2018/12", consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<Inntektsmelding>> opprettInntektsmeldinger201812(
            @RequestParam String eier,
            @RequestBody List<RsInntektsmelding> meldinger
    ) {
        try {
            InntektsmeldingRequestValidator.validate(meldinger, MeldingsType.TYPE_2018_12, eier);
        } catch (ValidationException e) {
            String errorstring = "Kunne ikke opprette inntektsmelding:\n" + e.getErrors();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorstring);
        }

        return ResponseEntity.ok(
                service.saveMeldinger(meldinger, MeldingsType.TYPE_2018_12, eier)
        );
    }

    @PostMapping(value = "/map/2018/12", consumes = "application/json", produces = "application/xml")
    public String mapInntektsmelding201812(
            @RequestBody RsInntektsmelding melding
    ) {

        return jaxbObjectToXML(XmlInntektsmelding201812.createInntektsmelding(melding));
    }

    @PostMapping(value = "/map/2018/09", consumes = "application/json", produces = "application/xml")
    public no.seres.xsd.nav.inntektsmelding_m._20180924.XMLInntektsmeldingM mapInntektsmelding201809(
            @RequestBody RsInntektsmelding melding
    ) {
        return XmlInntektsmelding201809.createInntektsmelding(melding);
    }


    private static String jaxbObjectToXML(Medling inntektsmelding) {
        try {
            //Create JAXB Context
            JAXBContext jaxbContext = JAXBContext.newInstance(Medling.class);

            //Create Marshaller
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            //Required formatting??
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            //Print XML String to Console
            StringWriter sw = new StringWriter();

            //Write XML to StringWriter
            jaxbMarshaller.marshal(inntektsmelding, sw);

            //Verify XML Content
            String xmlContent = sw.toString();
            System.out.println(xmlContent);
            return xmlContent;
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
