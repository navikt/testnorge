package no.nav.registre.inntektsmeldingstub.provider;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

import no.nav.registre.inntektsmeldingstub.provider.rs.RsInntektsmelding;
import no.nav.registre.inntektsmeldingstub.util.XmlInntektsmelding201812;

@RestController
@RequestMapping("/api/v1/inntektsmelding")
@RequiredArgsConstructor
public class InntektsmeldingController {

    @PostMapping(value = "/map/2018/12", consumes = "application/json", produces = "application/xml")
    public String mapInntektsmelding201812(
            @RequestBody RsInntektsmelding melding
    ) {

        return jaxbObjectToXML(XmlInntektsmelding201812.createInntektsmelding(melding));
    }

    private static String jaxbObjectToXML(Melding inntektsmelding) {
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
