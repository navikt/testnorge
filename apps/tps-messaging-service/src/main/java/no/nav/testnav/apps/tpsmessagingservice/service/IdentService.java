package no.nav.testnav.apps.tpsmessagingservice.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpsmessagingservice.consumer.ServicerutineConsumer;
import no.nav.testnav.apps.tpsmessagingservice.consumer.TestmiljoerServiceConsumer;
import no.nav.testnav.apps.tpsmessagingservice.consumer.command.TpsMeldingCommand;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsServicerutineM201Response;
import no.nav.testnav.apps.tpsmessagingservice.exception.BadRequestException;
import no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.TpsIdentStatusDTO;
import no.nav.tps.ctg.m201.domain.SRnavn;
import no.nav.tps.ctg.m201.domain.TpsPersonData;
import no.nav.tps.ctg.m201.domain.TpsServiceRutineType;
import org.springframework.stereotype.Service;

import javax.xml.namespace.QName;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
public class IdentService {

    private static final String SERVICE_NAME_OLD = "FS_03_FDLISTER_DISKNAVN_M";
    private static final String SERVICE_NAME_NEW = "FS03-FDLISTER-DISKNAVN-M";
    private static final String EMPTY_FNR = "<NFnr/>";
    private static final int MAX_LIMIT = 80;
    private static final String BAD_REQUEST = "Antall identer kan ikke være større enn " + MAX_LIMIT;
    private static final String PROD = "p";
    private static final String PROD_LIKE_ENV = "q2";

    private final ServicerutineConsumer servicerutineConsumer;
    private final JAXBContext requestContext;
    private final TestmiljoerServiceConsumer testmiljoerServiceConsumer;
    private final XmlMapper xmlMapper;

    public IdentService(ServicerutineConsumer servicerutineConsumer,
                        TestmiljoerServiceConsumer testmiljoerServiceConsumer)
            throws JAXBException {
        this.servicerutineConsumer = servicerutineConsumer;
        this.testmiljoerServiceConsumer = testmiljoerServiceConsumer;
        this.requestContext = JAXBContext.newInstance(TpsPersonData.class);
        this.xmlMapper = XmlMapper
                .builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .build();
    }

    public List<TpsIdentStatusDTO> getIdenter(List<String> identer, List<String> miljoer, Boolean includeProd) {

        if (identer.size() > MAX_LIMIT) {
            throw new BadRequestException(BAD_REQUEST);
        }

        Map<String, TpsServicerutineM201Response> tpsResponse = new HashMap<>();

        if (isNull(miljoer) || !miljoer.contains("pp")) {
            tpsResponse.putAll(readFromTps(identer, isNull(miljoer) ?
                    testmiljoerServiceConsumer.getMiljoer() : miljoer, false));
        }

        if (isTrue(includeProd)) {
            tpsResponse.put(PROD, readFromTps(identer, List.of(PROD_LIKE_ENV), true).get(PROD_LIKE_ENV));
        }

        return identer.parallelStream()
                .map(ident -> TpsIdentStatusDTO.builder()
                        .ident(ident)
                        .miljoer(tpsResponse.entrySet().parallelStream()
                                .filter(entry -> exists(ident, entry.getValue()))
                                .map(Map.Entry::getKey)
                                .toList())
                        .build())
                .toList();
    }

    @SneakyThrows
    public static String marshallToXML(JAXBContext requestContext, TpsPersonData endringsmelding) {

        var marshaller = requestContext.createMarshaller();

        var writer = new StringWriter();
        JAXBElement<TpsPersonData> element = new JAXBElement<>(new QName("", "tpsPersonData"), TpsPersonData.class, endringsmelding);
        marshaller.marshal(element, writer);

        return writer.toString();
    }

    private boolean exists(String ident, TpsServicerutineM201Response response) {

        return nonNull(response.getTpsSvar()) &&
                nonNull(response.getTpsSvar().getPersonDataM201()) &&
                nonNull(response.getTpsSvar().getPersonDataM201().getAFnr()) &&
                response.getTpsSvar().getPersonDataM201().getAFnr().getEFnr().stream()
                        .anyMatch(eFnr -> ident.equals(eFnr.getFnr()) && isNull(eFnr.getSvarStatus()));
    }

    private Map<String, TpsServicerutineM201Response> readFromTps(List<String> identer, List<String> miljoer, boolean isProd) {

        var request = prepareRequest(identer, isProd);
        var xmlRequest = marshallToXML(requestContext, request)
                .replace(SERVICE_NAME_OLD, SERVICE_NAME_NEW)
                .replace(EMPTY_FNR, "<nFnr>%s</nFnr>".formatted(identer.stream()
                        .map("<fnr>%s</fnr>"::formatted)
                        .collect(Collectors.joining(""))));

        var miljoerResponse = servicerutineConsumer.sendMessage(xmlRequest, miljoer);

        miljoerResponse.forEach((key, value) -> {
            if (value.contains("<returStatus>00</returStatus>") ||
                    value.contains("<returStatus>04</returStatus>")) {
                log.info("Miljø: {} XML: {}", key, value);
            } else {
                log.error("Miljø: {} XML: {}", key, value);
            }
        });

        return miljoerResponse.entrySet().parallelStream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> unmarshallFromXml(entry.getValue())));
    }

    @SneakyThrows
    private TpsServicerutineM201Response unmarshallFromXml(String endringsmeldingResponse) {

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

    private TpsPersonData prepareRequest(List<String> identer, boolean isProd) {

        var request = new TpsPersonData();
        request.setTpsServiceRutine(new TpsServiceRutineType());
        request.getTpsServiceRutine().setServiceRutinenavn(SRnavn.FS_03_FDLISTER_DISKNAVN_M);
        request.getTpsServiceRutine().setAksjonsKode("A");
        request.getTpsServiceRutine().setAksjonsKode2(isProd ? "2" : "0");
        request.getTpsServiceRutine().setAntallFnr(Integer.toString(identer.size()));
        request.getTpsServiceRutine().setNFnr(new TpsServiceRutineType.NFnr());
        request.getTpsServiceRutine().getNFnr().getFnr().addAll(identer);

        return request;
    }
}
