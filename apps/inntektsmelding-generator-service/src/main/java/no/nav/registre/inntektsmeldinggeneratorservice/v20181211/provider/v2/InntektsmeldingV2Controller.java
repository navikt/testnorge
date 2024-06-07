package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.provider.v2;

import io.swagger.v3.core.util.Json;
import jakarta.xml.bind.JAXBElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.registre.inntektsmeldinggeneratorservice.util.XmlConverter;
import no.nav.registre.inntektsmeldinggeneratorservice.v20181211.dto.v1.InntektsmeldingDTO;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.InntektsmeldingM;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsInntektsmelding;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/v2/inntektsmelding/2018/12/11")
@RequiredArgsConstructor
public class InntektsmeldingV2Controller {

    private final MapperFacade mapperFacade;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> create(@RequestBody RsInntektsmelding request) {

        log.info("Mottok inntektsmelding: {}", Json.pretty(request));

        var inntektsmelding = mapperFacade.map(request, InntektsmeldingDTO.class);

        JAXBElement<InntektsmeldingM> melding = inntektsmelding.toMelding();
        log.info("Konverterer inntektsmelding til : {}", melding);
        String xml = XmlConverter.toXml(melding, InntektsmeldingM.class);

//        if (!XmlConverter.validate(xml, InntektsmeldingM.class)) {
//            log.warn("Validering av opprett xml feilet");
//            return ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Validering av opprett xml feilet");
//        }
        log.info("Genererte  for inntektsmelding: {}", xml);

        return ResponseEntity.ok(xml);
    }
}