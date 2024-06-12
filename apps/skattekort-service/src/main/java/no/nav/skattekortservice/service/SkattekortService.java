package no.nav.skattekortservice.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.skattekortservice.consumer.SokosSkattekortConsumer;
import no.nav.skattekortservice.dto.SkattekortRequest;
import no.nav.skattekortservice.dto.SokosRequest;
import no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortTilArbeidsgiverDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;

@Slf4j
@Service
public class SkattekortService {

    private final JAXBContext jaxbContext;
    private final MapperFacade mapperFacade;
    private final SokosSkattekortConsumer skattekortConsumer;

    public SkattekortService(MapperFacade mapperFacade,
                             SokosSkattekortConsumer skattekortConsumer) throws JAXBException {
        this.jaxbContext = JAXBContext.newInstance(SkattekortRequest.class);
        this.mapperFacade = mapperFacade;
        this.skattekortConsumer = skattekortConsumer;
    }

    @SneakyThrows
    public Mono<String> sendSkattekort(SkattekortTilArbeidsgiverDTO skattekort) {

        validate(skattekort);
        var arbeidstaker = skattekort.getArbeidsgiver().getFirst()
                .getArbeidstaker().getFirst();

        return Mono.just(mapperFacade.map(skattekort, SkattekortRequest.class))
                .map(this::marshallToXml)
                .doOnNext(xmlRequest -> log.info("XML Request: {}", xmlRequest))
                .map(this::encodeRequest)
                .doOnNext(encodedXml -> log.info("Base64 encoded request: {}", encodedXml))
                .map(encodedXml -> SokosRequest.builder()
                        .fnr(arbeidstaker.getArbeidstakeridentifikator())
                        .inntektsar(arbeidstaker.getInntektsaar())
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

    private static void validate(SkattekortTilArbeidsgiverDTO skattekort) {

        skattekort.getArbeidsgiver().stream()
                .map(SkattekortTilArbeidsgiverDTO.Arbeidsgiver::getArbeidstaker)
                .flatMap(Collection::stream)
                .map(SkattekortTilArbeidsgiverDTO.Skattekortmelding::getSkattekort)
                .map(SkattekortTilArbeidsgiverDTO.Skattekort::getTrekktype)
                .flatMap(Collection::stream)
                .forEach(trekktype -> {
                    if (trekktype.isAllEmpty()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "En av Forskuddstrekk, Frikort, Trekkprosent og Trekktabell må angis per trekktype");
                    } else if (trekktype.isAmbiguous()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Kun én av Forskuddstrekk, Frikort, Trekkprosent og Trekktabell kan angis per trekktype");
                    }
                });
    }
}
