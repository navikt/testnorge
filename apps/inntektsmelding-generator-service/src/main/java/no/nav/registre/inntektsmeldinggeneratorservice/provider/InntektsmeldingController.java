package no.nav.registre.inntektsmeldinggeneratorservice.provider;

import io.swagger.v3.core.util.Json;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inntektsmeldinggeneratorservice.exception.JaxbToXmlException;
import no.nav.registre.inntektsmeldinggeneratorservice.util.XmlInntektsmelding201812;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsInntektsmelding;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.StringWriter;

@RestController
@RequestMapping("/api/v1/inntektsmelding")
@RequiredArgsConstructor
@Slf4j
public class InntektsmeldingController {

    @PostMapping(value = "/map/2018/12", consumes = "application/json", produces = "application/xml")
    public String mapInntektsmelding201812(
            @RequestBody RsInntektsmelding melding
    ) {

        return jaxbObjectToXML(XmlInntektsmelding201812.createInntektsmelding(melding));
    }

    private static String jaxbObjectToXML(Melding inntektsmelding) {
        log.info("Konverterter inntektsmelding til xml: {}", Json.pretty(inntektsmelding));
        try {
            //Create JAXB Context
            JAXBContext jaxbContext = JAXBContext.newInstance(Melding.class);

            //Create Marshaller
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            //Required formatting??
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            //Print XML String to Console
            StringWriter sw = new StringWriter();

            //Write XML to StringWriter
            jaxbMarshaller.marshal(inntektsmelding, sw);

            log.info("Generert xml: {}", sw);
            //Return XML Content
            return sw.toString();
        } catch (JAXBException e) {
            throw new JaxbToXmlException("Klarte ikke å konvertere inntektsmelding til XML", e);
        }
    }

}
