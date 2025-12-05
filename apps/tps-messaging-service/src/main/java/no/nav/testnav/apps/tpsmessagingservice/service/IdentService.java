package no.nav.testnav.apps.tpsmessagingservice.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpsmessagingservice.consumer.ServicerutineConsumer;
import no.nav.testnav.apps.tpsmessagingservice.consumer.TestmiljoerServiceConsumer;
import no.nav.testnav.apps.tpsmessagingservice.consumer.command.TpsMeldingCommand;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsServicerutineM201Response;
import no.nav.testnav.apps.tpsmessagingservice.exception.BadRequestException;
import no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsIdentStatusDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
public class IdentService {

    private static final int MAX_LIMIT = 80;
    private static final String BAD_REQUEST = "Antall identer kan ikke være større enn " + MAX_LIMIT;
    private static final String PROD = "p";
    private static final String PROD_LIKE_ENV = "q2";

    private final ServicerutineConsumer servicerutineConsumer;
    private final TestmiljoerServiceConsumer testmiljoerServiceConsumer;
    private final XmlMapper xmlMapper;

    public IdentService(ServicerutineConsumer servicerutineConsumer,
                        TestmiljoerServiceConsumer testmiljoerServiceConsumer) {

        this.servicerutineConsumer = servicerutineConsumer;
        this.testmiljoerServiceConsumer = testmiljoerServiceConsumer;
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

        return identer.stream()
                .map(ident -> TpsIdentStatusDTO.builder()
                        .ident(ident)
                        .miljoer(tpsResponse.entrySet().stream()
                                .filter(entry -> exists(ident, entry.getValue()))
                                .map(Map.Entry::getKey)
                                .toList())
                        .status(tpsResponse.values().stream()
                                .filter(tpsServicerutineM201Response ->
                                        isNotBlank(tpsServicerutineM201Response.getTpsSvar().getSvarStatus().getUtfyllendeMelding()) &&
                                        !"Person ikke funnet".equalsIgnoreCase(tpsServicerutineM201Response.getTpsSvar().getSvarStatus().getUtfyllendeMelding()))
                                .map(tpsServicerutineM201Response -> tpsServicerutineM201Response.getTpsSvar().getSvarStatus().getUtfyllendeMelding())
                                .findFirst().orElse(null))
                        .build())
                .toList();
    }

    private boolean exists(String ident, TpsServicerutineM201Response response) {

        return nonNull(response.getTpsSvar()) &&
                nonNull(response.getTpsSvar().getPersonDataM201()) &&
                nonNull(response.getTpsSvar().getPersonDataM201().getAFnr()) &&
                response.getTpsSvar().getPersonDataM201().getAFnr().getEFnr().stream()
                        .anyMatch(eFnr -> ident.equals(eFnr.getFnr()) && isNull(eFnr.getSvarStatus()));
    }

    private Map<String, TpsServicerutineM201Response> readFromTps(List<String> identer, List<String> miljoer, boolean isProd) {

        var xmlRequest = prepareRequest(identer, isProd);

        var miljoerResponse = servicerutineConsumer.sendMessage(xmlRequest, miljoer);

        miljoerResponse.forEach((key, value) -> log.info("Miljø: {} XML: {}", key, value));

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

    private String prepareRequest(List<String> identer, boolean isProd) {

        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<tpsPersonData>" +
                "<tpsServiceRutine>" +
                "<serviceRutinenavn>FS03-FDLISTER-DISKNAVN-M</serviceRutinenavn>" +
                "<aksjonsKode>A</aksjonsKode>" +
                "<aksjonsKode2>%s</aksjonsKode2><antallFnr>%s</antallFnr><nFnr>%s</nFnr><systemId>Dolly</systemId></tpsServiceRutine></tpsPersonData>"
                        .formatted(isProd ? "2" : "0",
                                Integer.toString(identer.size()),
                                identer.stream()
                                        .map("<fnr>%s</fnr>"::formatted)
                                        .collect(Collectors.joining()));
    }
}
