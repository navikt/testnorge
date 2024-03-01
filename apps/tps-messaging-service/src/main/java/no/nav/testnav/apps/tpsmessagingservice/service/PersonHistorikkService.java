package no.nav.testnav.apps.tpsmessagingservice.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.apps.tpsmessagingservice.consumer.ServicerutineConsumer;
import no.nav.testnav.apps.tpsmessagingservice.consumer.TestmiljoerServiceConsumer;
import no.nav.testnav.apps.tpsmessagingservice.consumer.command.TpsMeldingCommand;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsServiceRutine;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsServicerutineRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsServicerutineS018Response;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsServicerutineS610Response;
import no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil;
import no.nav.testnav.apps.tpsmessagingservice.utils.ServiceRutineUtil;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.PersonhistorikkDTO;
import no.nav.tps.xjc.ctg.domain.s018.SRnavnType;
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
    private final XmlMapper xmlMapper;
    private final MapperFacade mapperFacade;

    public PersonHistorikkService(TestmiljoerServiceConsumer testmiljoerServiceConsumer,
                                  ServicerutineConsumer servicerutineConsumer,
                                  XmlMapper xmlMapper,
                                  MapperFacade mapperFacade) throws JAXBException {

        this.testmiljoerServiceConsumer = testmiljoerServiceConsumer;
        this.requestContext = JAXBContext.newInstance(TpsServicerutineRequest.class);
        this.servicerutineConsumer = servicerutineConsumer;
        this.xmlMapper = xmlMapper;
        this.mapperFacade = mapperFacade;
    }

    public List<PersonhistorikkDTO> hentIdent(String ident, LocalDate aksjonsdato, List<String> miljoer) {

        if (miljoer.isEmpty()) {
            miljoer = testmiljoerServiceConsumer.getMiljoer();
        }

        var tpsPersoner = readFromTps(ident, aksjonsdato, miljoer);

        return tpsPersoner.entrySet().stream()
                .map(entry -> PersonhistorikkDTO.builder()
                        .miljoe(entry.getKey())
                        .persondata(mapperFacade.map(entry.getValue(), PersonhistorikkDTO.PersonData.class))
                        .build())
                .toList();
    }

    private Map<String, TpsServicerutineS018Response> readFromTps(String ident, LocalDate foedselsdato, List<String> miljoer) {

        var request = TpsServicerutineRequest.builder()
                .tpsServiceRutine(TpsServiceRutine.builder()
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
                    .tpsPersonData(TpsServicerutineS018Response.TpsPersonData.builder()
                            .tpsSvar(TpsServicerutineS610Response.TpsSvar.builder()
                                    .svarStatus(EndringsmeldingUtil.getNoAnswerStatus())
                                    .build())
                            .build())
                    .build();
        } else {

            return xmlMapper.readValue(endringsmeldingResponse, TpsServicerutineS018Response.class);
        }
    }
}
