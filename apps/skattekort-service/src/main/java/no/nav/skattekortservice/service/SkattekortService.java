package no.nav.skattekortservice.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.skattekortservice.consumer.SokosSkattekortConsumer;
import no.nav.skattekortservice.dto.SkattekortRequest;
import no.nav.skattekortservice.dto.SkattekortResponsIntermediate;
import no.nav.skattekortservice.dto.SokosRequest;
import no.nav.skattekortservice.dto.SokosResponse;
import no.nav.skattekortservice.utility.SkattekortValidator;
import no.nav.testnav.libs.dto.skattekortservice.v1.ArbeidsgiverSkatt;
import no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortRequestDTO;
import no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortResponseDTO;
import org.json.XML;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

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

    public Flux<SkattekortResponseDTO> hentSkattekort(String ident) {

        return skattekortConsumer.hentSkattekort(ident)
                .doOnNext(response -> log.info("Hentet resultat fra Sokos {}", response))
                .map(this::convertResponse);
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

    private SkattekortResponseDTO convertResponse(SokosResponse response) {

        var decoded = decodeRequest(response.getSkattekort());
        var unmarshalled = unmarshal(decoded);
        return SkattekortResponseDTO.builder()
                .ident(response.getFnr())
                .inntektsaar(response.getInntektsar())
                .arbeidsgiver(unmarshalled)
                .skattekortXml(decoded)
                .build();
    }

    private String decodeRequest(String request) {

        return new String(Base64.getDecoder().decode(request), StandardCharsets.UTF_8)
                .replaceAll("\n *", "");
    }

    @SneakyThrows
    private List<ArbeidsgiverSkatt> unmarshal(String xmlData) {

        var jsonRoot = XML.toJSONObject(xmlData);
        var intermediate = objectMapper.readValue(jsonRoot.toString(), SkattekortResponsIntermediate.class);
        return mapperFacade.mapAsList(intermediate.getSkattekortTilArbeidsgiver().getArbeidsgiver(), ArbeidsgiverSkatt.class);
    }
}