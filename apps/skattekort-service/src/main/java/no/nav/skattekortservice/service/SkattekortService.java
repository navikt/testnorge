package no.nav.skattekortservice.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.skattekortservice.dto.SkattekortRequest;
import no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortTilArbeidsgiverDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.StringWriter;
import java.util.Collection;

@Slf4j
@Service
public class SkattekortService {

    private final JAXBContext jaxbContext;
    private final MapperFacade mapperFacade;

    public SkattekortService(MapperFacade mapperFacade) throws JAXBException {
        this.jaxbContext = JAXBContext.newInstance(SkattekortRequest.class);
        this.mapperFacade = mapperFacade;
    }

    @SneakyThrows
    public Object sendSkattekort(SkattekortTilArbeidsgiverDTO skattekort) {

        validate(skattekort);

        var request = mapperFacade.map(skattekort, SkattekortRequest.class);

        var xmlRequest = marshallToXml(request);
        log.info("XML Request: {}", xmlRequest);

        //TBD send melding
        return "XML har blitt generert og logget, men innsending av skattekort mangler i påvente av nytt API hos team motta-og-beregne.";
    }

    @SneakyThrows
    private String marshallToXml(SkattekortRequest melding) {

        var marshaller = jaxbContext.createMarshaller();
        var writer = new StringWriter();
        marshaller.marshal(melding, writer);
        return writer.toString();
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
