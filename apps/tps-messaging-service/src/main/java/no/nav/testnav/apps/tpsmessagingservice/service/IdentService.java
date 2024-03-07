package no.nav.testnav.apps.tpsmessagingservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpsmessagingservice.consumer.ServicerutineConsumer;
import no.nav.testnav.apps.tpsmessagingservice.consumer.TestmiljoerServiceConsumer;
import no.nav.testnav.apps.tpsmessagingservice.consumer.command.TpsMeldingCommand;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsServicerutineM201Response;
import no.nav.testnav.apps.tpsmessagingservice.exception.BadRequestException;
import no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.TpsIdentStatusDTO;
import org.json.XML;
import org.springframework.stereotype.Service;

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

    private static final int MAX_LIMIT = 80;
    private static final String BAD_REQUEST = "Antall identer kan ikke være større enn " + MAX_LIMIT;
    private static final String PROD = "p";
    private static final String PROD_LIKE_ENV = "q2";

    private final ServicerutineConsumer servicerutineConsumer;
    private final TestmiljoerServiceConsumer testmiljoerServiceConsumer;
    private final ObjectMapper objectMapper;

    public IdentService(ServicerutineConsumer servicerutineConsumer,
                        TestmiljoerServiceConsumer testmiljoerServiceConsumer,
                        ObjectMapper objectMapper) {

        this.servicerutineConsumer = servicerutineConsumer;
        this.testmiljoerServiceConsumer = testmiljoerServiceConsumer;
        this.objectMapper = objectMapper;
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

    private boolean exists(String ident, TpsServicerutineM201Response response) {

        return nonNull(response.getTpsPersonData()) && nonNull(response.getTpsPersonData().getTpsSvar()) &&
                nonNull(response.getTpsPersonData().getTpsSvar().getPersonDataM201()) &&
                nonNull(response.getTpsPersonData().getTpsSvar().getPersonDataM201().getAFnr()) &&
                response.getTpsPersonData().getTpsSvar().getPersonDataM201().getAFnr().getEFnr().stream()
                        .anyMatch(eFnr -> ident.equals(eFnr.getFnr()) && isNull(eFnr.getSvarStatus()));
    }

    private Map<String, TpsServicerutineM201Response> readFromTps(List<String> identer, List<String> miljoer, boolean isProd) {

        var xmlRequest = prepareRequest(identer, isProd);

        log.info("M201 request: {}", xmlRequest);

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
                    .tpsPersonData(TpsServicerutineM201Response.TpsPersonData.builder()
                            .tpsSvar(TpsServicerutineM201Response.TpsSvar.builder()
                                    .svarStatus(EndringsmeldingUtil.getNoAnswerStatus())
                                    .build())
                            .build())
                    .build();
        } else {

            var jsonRoot = XML.toJSONObject(endringsmeldingResponse);
            return objectMapper.readValue(jsonRoot.toString(), TpsServicerutineM201Response.class);
        }
    }

    private String prepareRequest(List<String> identer, boolean isProd) {

        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<tpsPersonData>" +
                "<tpsServiceRutine>" +
                "<serviceRutinenavn>FS03-FDLISTER-DISKNAVN-M</serviceRutinenavn>" +
                "<aksjonsKode>A</aksjonsKode>" +
                "<aksjonsKode2>%s</aksjonsKode2><antallFnr>%s</antallFnr><nFnr>%s</nFnr></tpsServiceRutine></tpsPersonData>"
                        .formatted(isProd ? "2" : "0",
                                Integer.toString(identer.size()),
                                identer.stream()
                                        .map("<fnr>%s</fnr>"::formatted)
                                        .collect(Collectors.joining()));
    }
}
