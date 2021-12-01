package no.nav.testnav.apps.tpsmessagingservice.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpsmessagingservice.consumer.ServicerutineConsumer;
import no.nav.testnav.apps.tpsmessagingservice.consumer.command.TpsMeldingCommand;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsServicerutineM201Response;
import no.nav.testnav.apps.tpsmessagingservice.exception.BadRequestException;
import no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsIdentStatusDTO;
import no.nav.tps.ctg.m201.domain.SRnavn;
import no.nav.tps.ctg.m201.domain.TpsPersonData;
import no.nav.tps.ctg.m201.domain.TpsServiceRutineType;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
public class IdentService {

    private static final int MAX_LIMIT = 80;
    private static final String BAD_REQUEST = "Antall identer kan ikke være større enn " + MAX_LIMIT;
    private static final String STATUS_OK = "00";

    private final ServicerutineConsumer servicerutineConsumer;
    private final JAXBContext requestContext;
    private final JAXBContext responseContext;
    private final MiljoerService miljoerService;
    private final XmlMapper xmlMapper;

    public IdentService(ServicerutineConsumer servicerutineConsumer, MiljoerService miljoerService, XmlMapper xmlMapper) throws JAXBException {
        this.servicerutineConsumer = servicerutineConsumer;
        this.miljoerService = miljoerService;
        this.requestContext = JAXBContext.newInstance(TpsPersonData.class);
        this.responseContext = JAXBContext.newInstance(TpsPersonData.class);
        this.xmlMapper = xmlMapper;
    }

    @SneakyThrows
    public static String marshallToXML(JAXBContext requestContext, TpsPersonData endringsmelding) {

        var marshaller = requestContext.createMarshaller();

        var writer = new StringWriter();
        marshaller.marshal(endringsmelding, writer);

        return writer.toString();
    }

    public List<TpsIdentStatusDTO> getIdenter(List<String> identer, List<String> miljoer, Boolean includeProd) {

        if (identer.size() > MAX_LIMIT) {
            throw new BadRequestException(BAD_REQUEST);
        }
        var tpsResponse = readFromTps(identer, isNull(miljoer) ? miljoerService.getMiljoer() : miljoer);

        return identer.parallelStream()
                .map(ident -> TpsIdentStatusDTO.builder()
                        .ident(ident)
                        .miljoer(tpsResponse.entrySet().parallelStream()
                                .filter(entry -> nonNull(entry.getValue().getTpsSvar().getPersonDataM201()) &&
                                        nonNull(entry.getValue().getTpsSvar().getPersonDataM201().getAFnr()) &&
                                        entry.getValue().getTpsSvar().getPersonDataM201().getAFnr().getEFnr().stream()
                                                .anyMatch(eFnr -> ident.equals(eFnr.getFnr()) && isNotBlank(eFnr.getKn())))
                                .map(entry -> entry.getKey())
                                .toList())
                        .build())
                .toList();
    }

    private Map<String, TpsServicerutineM201Response> readFromTps(List<String> identer, List<String> miljoer) {

        var request = prepareRequest(identer);
        var xmlRequest = marshallToXML(requestContext, request);

        var miljoerResponse = servicerutineConsumer.sendMessage(xmlRequest, miljoer);

        miljoerResponse.entrySet().stream()
                .forEach(entry -> log.info("Miljø: {} XML: {}", entry.getKey(), entry.getValue()));

        return miljoerResponse.entrySet().parallelStream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> {
                            try {
                                return unmarshallFromXml(entry.getValue());
                            } catch (JAXBException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }));
    }

    @SneakyThrows
    private TpsServicerutineM201Response unmarshallFromXml(String endringsmeldingResponse) throws JAXBException {

        if (TpsMeldingCommand.NO_RESPONSE.equals(endringsmeldingResponse)) {

            return TpsServicerutineM201Response.builder()
                    .tpsSvar(TpsServicerutineM201Response.TpsSvar.builder()
                            .svarStatus(EndringsmeldingUtil.getNoAnswerStatus())
                            .build())
                    .build();
        } else {

            return xmlMapper.readValue(endringsmeldingResponse, TpsServicerutineM201Response.class);
        }
    }

    private TpsPersonData prepareRequest(List<String> identer) {

        var request = new TpsPersonData();
        request.setTpsServiceRutine(new TpsServiceRutineType());
        request.getTpsServiceRutine().setServiceRutinenavn(SRnavn.FS_03_FDLISTER_DISKNAVN_M);
        request.getTpsServiceRutine().setAksjonsKode("A");
        request.getTpsServiceRutine().setAksjonsKode2("0");
        request.getTpsServiceRutine().setAntallFnr(Integer.toString(identer.size()));
        request.getTpsServiceRutine().setNFnr(new TpsServiceRutineType.NFnr());
        request.getTpsServiceRutine().getNFnr().getFnr().addAll(identer);

        return request;
    }
}
