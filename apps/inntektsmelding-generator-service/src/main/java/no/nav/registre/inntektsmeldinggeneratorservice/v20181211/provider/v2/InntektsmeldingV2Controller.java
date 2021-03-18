package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.provider.v2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLInntektsmeldingM;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBElement;

import no.nav.registre.inntektsmeldinggeneratorservice.util.XmlConverter;
import no.nav.registre.inntektsmeldinggeneratorservice.v20181211.dto.v1.InntektsmeldingDTO;

@Slf4j
@RestController
@RequestMapping("/api/v2/inntektsmelding/2018/12/11")
@RequiredArgsConstructor
public class InntektsmeldingV2Controller {

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> create(@RequestBody InntektsmeldingDTO inntektsmeldingDTO) {
        JAXBElement<XMLInntektsmeldingM> melding = inntektsmeldingDTO.toMelding();
        String xml = XmlConverter.toXml(melding, XMLInntektsmeldingM.class);

        if (!XmlConverter.validate(xml, XMLInntektsmeldingM.class)) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Validering av opprett xml feilet");
        }

        return ResponseEntity.ok(xml);
    }
}
