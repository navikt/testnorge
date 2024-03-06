package no.nav.testnav.apps.tpsmessagingservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.apps.tpsmessagingservice.consumer.ServicerutineConsumer;
import no.nav.testnav.apps.tpsmessagingservice.consumer.TestmiljoerServiceConsumer;
import no.nav.testnav.apps.tpsmessagingservice.consumer.command.TpsMeldingCommand;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsServicerutineAksjonsdatoRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsServicerutineS018Response;
import no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil;
import no.nav.testnav.apps.tpsmessagingservice.utils.ServiceRutineUtil;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.PersonhistorikkDTO;
import no.nav.tps.xjc.ctg.domain.s018.SRnavnType;
import org.json.XML;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PersonHistorikkService {

    private final TestmiljoerServiceConsumer testmiljoerServiceConsumer;
    private final JAXBContext requestContext;
    private final ServicerutineConsumer servicerutineConsumer;
    private final MapperFacade mapperFacade;
    private final ObjectMapper objectMapper;

    public PersonHistorikkService(TestmiljoerServiceConsumer testmiljoerServiceConsumer,
                                  ServicerutineConsumer servicerutineConsumer,
                                  MapperFacade mapperFacade,
                                  ObjectMapper objectMapper) throws JAXBException {

        this.testmiljoerServiceConsumer = testmiljoerServiceConsumer;
        this.requestContext = JAXBContext.newInstance(TpsServicerutineAksjonsdatoRequest.class);
        this.servicerutineConsumer = servicerutineConsumer;
        this.mapperFacade = mapperFacade;
        this.objectMapper = objectMapper;
    }

    public List<PersonhistorikkDTO> hentIdent(String ident, LocalDate aksjonsdato, List<String> miljoer) {

        if (miljoer.isEmpty()) {
            miljoer = testmiljoerServiceConsumer.getMiljoer();
        }

        var tpsPersoner = readFromTps(ident, aksjonsdato, miljoer);

        return tpsPersoner.entrySet().stream()
                .map(entry -> PersonhistorikkDTO.builder()
                        .miljoe(entry.getKey())
                        .status(mapperFacade.map(entry.getValue().getTpsSvar(), PersonhistorikkDTO.TpsMeldingResponse.class))
                        .persondata("OK".equals(entry.getValue().getTpsSvar().getSvarStatus().getReturStatus()) ?
                                mapperFacade.map(entry.getValue().getTpsSvar().getPersondataS018(), PersonhistorikkDTO.PersonData.class) :
                                null)
                        .build())
                .toList();
    }

    private Map<String, TpsServicerutineS018Response> readFromTps(String ident, LocalDate foedselsdato, List<String> miljoer) {

        var request = TpsServicerutineAksjonsdatoRequest.builder()
                .tpsServiceRutine(TpsServicerutineAksjonsdatoRequest.TpsServiceRutineMedAksjonsdato.builder()
                        .serviceRutinenavn(SRnavnType.FS_03_FDNUMMER_PADRHIST_O.name())
                        .fnr(ident)
                        .aksjonsKode("A")
                        .aksjonsKode2("0")
                        .aksjonsDato(foedselsdato.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .build())
                .build();

        var xmlRequest = ServiceRutineUtil.marshallToXML(requestContext, request);

        var miljoerResponse = servicerutineConsumer.sendMessage(xmlRequest, miljoer);

        miljoerResponse.forEach((key, value) -> log.info("Miljø: {} XML: {}", key, value));

        return miljoerResponse.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> unmarshallFromXml(entry.getValue())));
    }

    @SneakyThrows
    private TpsServicerutineS018Response unmarshallFromXml(String endringsmeldingResponse) {

        if (TpsMeldingCommand.NO_RESPONSE.equals(endringsmeldingResponse)) {

            return TpsServicerutineS018Response.builder()
                            .tpsSvar(TpsServicerutineS018Response.TpsSvar.builder()
                                    .svarStatus(EndringsmeldingUtil.getNoAnswerStatus())
                                    .build())
                            .build();

        } else {

            var jsonRoot = XML.toJSONObject(endringsmeldingResponse);
            return objectMapper.readValue(jsonRoot.toString(), TpsServicerutineS018Response.class);
        }
    }
}
