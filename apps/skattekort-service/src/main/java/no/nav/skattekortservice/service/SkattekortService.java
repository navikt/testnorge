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
import no.nav.skattekortservice.dto.SokosResponse;
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

//    public Mono<String> hentSkattekort(String ident, Integer inntektsaar) {
//
//        return skattekortConsumer.hentSkattekort(ident, inntektsaar)
//                .doOnNext(response -> log.info("Hentet resultat fra Sokos {}", response))
//                .map(data -> data.substring(data.indexOf("<?")));
//    }

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
//
//        var unmarshalled = unmarshal(xml);
//        var intermediate = mapperFacade.map(unmarshalled, SkattekortResponsIntermediate.class);
//        var arbeidsgiver = mapperFacade.mapAsList(intermediate.getSkattekortTilArbeidsgiver().getArbeidsgiver(), Arbeidsgiver.class);
//        return Flux.just(SkattekortResponseDTO.builder()
//                .ident(ident)
//                .inntektsaar("2023")
//                .arbeidsgiver(arbeidsgiver)
//                .skattekortXml(xml)
//                .build());

        return skattekortConsumer.hentSkattekort(ident)
                .doOnNext(response -> log.info("Hentet resultat fra Sokos {}", response))
                .map(this::unwrapResponse);
    }

    private SkattekortResponseDTO unwrapResponse(SokosResponse response) {

        var unmarshalled = unmarshal(response.getSkattekort());
        var intermediate = mapperFacade.map(unmarshalled, SkattekortResponsIntermediate.class);
        var arbeidsgiver = mapperFacade.mapAsList(intermediate.getSkattekortTilArbeidsgiver().getArbeidsgiver(), Arbeidsgiver.class);
        return SkattekortResponseDTO.builder()
                .ident(response.getFnr())
                .inntektsaar(response.getInntektsar())
                .arbeidsgiver(arbeidsgiver)
                .skattekortXml(response.getSkattekort())
                .build();
    }

    @SneakyThrows
    private SkattekortResponsIntermediate unmarshal(String xmlData) {

        var jsonRoot = XML.toJSONObject(xmlData);
        return objectMapper.readValue(jsonRoot.toString(), SkattekortResponsIntermediate.class);
    }
}