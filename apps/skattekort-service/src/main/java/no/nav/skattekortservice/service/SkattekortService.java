package no.nav.skattekortservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.skattekortservice.consumer.SokosSkattekortConsumer;
import no.nav.skattekortservice.dto.SkattekortRequest;
import no.nav.skattekortservice.dto.SkattekortResponsIntermediate;
import no.nav.skattekortservice.dto.SokosRequest;
import no.nav.skattekortservice.utility.SkattekortValidator;
import no.nav.testnav.libs.dto.skattekortservice.v1.Arbeidsgiver;
import no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortRequestDTO;
import no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortResponseDTO;
import org.json.XML;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
public class SkattekortService {

    private final JAXBContext jaxbContext;
    private final MapperFacade mapperFacade;
    private final SokosSkattekortConsumer skattekortConsumer;
    private final ObjectMapper objectMapper;

    public SkattekortService(MapperFacade mapperFacade,
                             SokosSkattekortConsumer skattekortConsumer,
                             ObjectMapper objectMapper) throws JAXBException {
        this.jaxbContext = JAXBContext.newInstance(SkattekortRequest.class);
        this.mapperFacade = mapperFacade;
        this.skattekortConsumer = skattekortConsumer;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    public Mono<String> sendSkattekort(SkattekortRequestDTO skattekort) {

        SkattekortValidator.validate(skattekort);

        var arbeidstaker = skattekort.getArbeidsgiver().getFirst()
                .getArbeidstaker().getFirst();

        return Mono.just(mapperFacade.map(skattekort, SkattekortRequest.class))
                .map(this::marshallToXml)
                .doOnNext(xmlRequest -> log.info("XML Request: {}", xmlRequest))
                .map(this::encodeRequest)
                .doOnNext(encodedXml -> log.info("Base64 encoded request: {}", encodedXml))
                .map(encodedXml -> SokosRequest.builder()
                        .fnr(arbeidstaker.getArbeidstakeridentifikator())
                        .inntektsar(arbeidstaker.getInntektsaar().toString())
                        .skattekort(encodedXml)
                        .build())
                .flatMap(skattekortConsumer::sendSkattekort);
    }

    @SneakyThrows
    private String marshallToXml(SkattekortRequest melding) {

        var marshaller = jaxbContext.createMarshaller();
        var writer = new StringWriter();
        marshaller.marshal(melding, writer);
        return writer.toString()
                .replace(":ns2", "")
                .replace("ns2:", "");
    }

    private String encodeRequest(String request) {

        return Base64.getEncoder()
                .encodeToString(request.getBytes(StandardCharsets.UTF_8));
    }

    public Flux<SkattekortResponseDTO> hentSkattekort(String ident) {

//        var xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><skattekortTilArbeidsgiver xmlns=\"urn:no:skatteetaten:fastsetting:formueinntekt:forskudd:skattekorttilarbeidsgiver:v3\"><arbeidsgiver><arbeidsgiveridentifikator><organisasjonsnummer>311734830</organisasjonsnummer></arbeidsgiveridentifikator><arbeidstaker><arbeidstakeridentifikator>19879098303</arbeidstakeridentifikator><resultatPaaForespoersel>skattekortopplysningerOK</resultatPaaForespoersel><skattekort><utstedtDato>2024-06-12</utstedtDato><skattekortidentifikator>121212</skattekortidentifikator><forskuddstrekk><trekkode>loennFraHovedarbeidsgiver</trekkode></forskuddstrekk></skattekort><tilleggsopplysning>oppholdPaaSvalbard</tilleggsopplysning><inntektsaar>2023</inntektsaar></arbeidstaker></arbeidsgiver></skattekortTilArbeidsgiver>";
//        var base64encoded = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/Pjxza2F0dGVrb3J0VGlsQXJiZWlkc2dpdmVyIHhtbG5zPSJ1cm46bm86c2thdHRlZXRhdGVuOmZhc3RzZXR0aW5nOmZvcm11ZWlubnRla3Q6Zm9yc2t1ZGQ6c2thdHRla29ydHRpbGFyYmVpZHNnaXZlcjp2MyI+PGFyYmVpZHNnaXZlcj48YXJiZWlkc2dpdmVyaWRlbnRpZmlrYXRvcj48b3JnYW5pc2Fzam9uc251bW1lcj45MTA5NjI3Mjg8L29yZ2FuaXNhc2pvbnNudW1tZXI+PC9hcmJlaWRzZ2l2ZXJpZGVudGlmaWthdG9yPjxhcmJlaWRzdGFrZXI+PGFyYmVpZHN0YWtlcmlkZW50aWZpa2F0b3I+MDIxMTg0MDAxOTc8L2FyYmVpZHN0YWtlcmlkZW50aWZpa2F0b3I+PHJlc3VsdGF0UGFhRm9yZXNwb2Vyc2VsPnNrYXR0ZWtvcnRvcHBseXNuaW5nZXJPSzwvcmVzdWx0YXRQYWFGb3Jlc3BvZXJzZWw+PHNrYXR0ZWtvcnQ+PHV0c3RlZHREYXRvPjIwMjEtMDMtMDY8L3V0c3RlZHREYXRvPjxza2F0dGVrb3J0aWRlbnRpZmlrYXRvcj4xNTc5ODk8L3NrYXR0ZWtvcnRpZGVudGlmaWthdG9yPjxmb3Jza3VkZHN0cmVrayB4c2k6dHlwZT0iVHJla2t0YWJlbGwiIHhtbG5zOnhzaT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEtaW5zdGFuY2UiPjx0cmVra29kZT5pbnRyb2R1a3Nqb25zc3RvZW5hZDwvdHJla2tvZGU+PHRhYmVsbHR5cGU+dHJla2t0YWJlbGxGb3JQZW5zam9uPC90YWJlbGx0eXBlPjx0YWJlbGxudW1tZXI+NzEwMDwvdGFiZWxsbnVtbWVyPjxwcm9zZW50c2F0cz4yMjwvcHJvc2VudHNhdHM+PGFudGFsbE1hYW5lZGVyRm9yVHJla2s+MTE8L2FudGFsbE1hYW5lZGVyRm9yVHJla2s+PC9mb3Jza3VkZHN0cmVraz48Zm9yc2t1ZGRzdHJla2sgeHNpOnR5cGU9IlRyZWtrcHJvc2VudCIgeG1sbnM6eHNpPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYS1pbnN0YW5jZSI+PHRyZWtrb2RlPmxvZW5uRnJhSG92ZWRhcmJlaWRzZ2l2ZXI8L3RyZWtrb2RlPjxwcm9zZW50c2F0cz4zMjwvcHJvc2VudHNhdHM+PC9mb3Jza3VkZHN0cmVraz48Zm9yc2t1ZGRzdHJla2sgeHNpOnR5cGU9IlRyZWtrcHJvc2VudCIgeG1sbnM6eHNpPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYS1pbnN0YW5jZSI+PHRyZWtrb2RlPmxvZW5uRnJhQmlhcmJlaWRzZ2l2ZXI8L3RyZWtrb2RlPjxwcm9zZW50c2F0cz4zMjwvcHJvc2VudHNhdHM+PC9mb3Jza3VkZHN0cmVraz48L3NrYXR0ZWtvcnQ+PGlubnRla3RzYWFyPjIwMjE8L2lubnRla3RzYWFyPjwvYXJiZWlkc3Rha2VyPjwvYXJiZWlkc2dpdmVyPjwvc2thdHRla29ydFRpbEFyYmVpZHNnaXZlcj4=";
//        var decodedXml = decodeRequest(base64encoded);
//
//        var arbeidsgivere = unmarshal(decodedXml);
//
//        return Flux.just(SkattekortResponseDTO.builder()
//                .ident(ident)
//                .inntektsaar("2023")
//                .arbeidsgiver(arbeidsgivere)
//                .skattekortXml(xml)
//                .build());

        return skattekortConsumer.hentSkattekort(ident)
                .doOnNext(response -> log.info("Hentet resultat fra Sokos {}", response))
                .map(response -> Flux.just(decodeRequest(response.getSkattekort()))
                        .map(this::unmarshal)
                        .map(arbeidsgivere -> SkattekortResponseDTO.builder()
                                .ident(response.getFnr())
                                .inntektsaar(response.getInntektsar())
                                .arbeidsgiver(arbeidsgivere)
                                .skattekortXml(response.getSkattekort())
                                .build()))
                .flatMap(Flux::from);
    }

    private String decodeRequest(String request) {

        return new String(Base64.getDecoder().decode(request), StandardCharsets.UTF_8);
    }

    @SneakyThrows
    private List<Arbeidsgiver> unmarshal(String xmlData) {

        var jsonRoot = XML.toJSONObject(xmlData);
        var intermediate = objectMapper.readValue(jsonRoot.toString(), SkattekortResponsIntermediate.class);
        return mapperFacade.mapAsList(intermediate.getSkattekortTilArbeidsgiver().getArbeidsgiver(), Arbeidsgiver.class);
    }
}