package no.nav.testnav.apps.tpsmessagingservice.service;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.apps.tpsmessagingservice.consumer.EndringsmeldingConsumer;
import no.nav.testnav.apps.tpsmessagingservice.consumer.command.TpsMeldingCommand;
import no.nav.testnav.apps.tpsmessagingservice.dto.KontaktopplysningerRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.KontaktopplysningerResponse;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsMeldingResponse;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsSystemInfo;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TelefonnummerDTO;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.getErrorStatus;
import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.getResponseStatus;
import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.marshallToXML;
import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.unmarshallFromXml;

@Slf4j
@Service
public class TelefonnummerService {

    private final MapperFacade mapperFacade;
    private final EndringsmeldingConsumer endringsmeldingConsumer;
    private final JAXBContext requestContext;
    private final JAXBContext responseContext;

    public TelefonnummerService(MapperFacade mapperFacade, EndringsmeldingConsumer endringsmeldingConsumer) throws JAXBException {
        this.mapperFacade = mapperFacade;
        this.endringsmeldingConsumer = endringsmeldingConsumer;

        this.requestContext = JAXBContext.newInstance(KontaktopplysningerRequest.class);
        this.responseContext = JAXBContext.newInstance(KontaktopplysningerResponse.class);
    }

    public Map<String, TpsMeldingResponse> endreTelefonnummer(String ident, List<TelefonnummerDTO> telefonnummer, List<String> miljoer) {

        return endreTelefonnummer(ident, KontaktopplysningerRequest.builder()
                .sfeAjourforing(KontaktopplysningerRequest.SfeAjourforing.builder()
                        .systemInfo(TpsSystemInfo.getDefault())
                        .endreKontaktopplysninger(KontaktopplysningerRequest.KontaktOpplysninger.builder()
                                .offentligIdent(ident)
                                .endringAvTelefon(KontaktopplysningerRequest.TelefonOpplysninger.builder()
                                        .nyTelefon(mapperFacade.mapAsList(telefonnummer, KontaktopplysningerRequest.TelefonData.class))
                                        .build())
                                .build())
                        .build())
                .build(), miljoer);
    }

    public Map<String, TpsMeldingResponse> opphoerTelefonnummer(String ident, List<TelefonnummerDTO.TypeTelefon> typer, List<String> miljoer) {

        return endreTelefonnummer(ident, KontaktopplysningerRequest.builder()
                .sfeAjourforing(KontaktopplysningerRequest.SfeAjourforing.builder()
                        .systemInfo(TpsSystemInfo.getDefault())
                        .endreKontaktopplysninger(KontaktopplysningerRequest.KontaktOpplysninger.builder()
                                .offentligIdent(ident)
                                .endringAvTelefon(KontaktopplysningerRequest.TelefonOpplysninger.builder()
                                        .opphorTelefon(mapperFacade.mapAsList(typer, KontaktopplysningerRequest.Telefontype.class))
                                        .build())
                                .build())
                        .build())
                .build(), miljoer);
    }

    private Map<String, TpsMeldingResponse> endreTelefonnummer(String ident, KontaktopplysningerRequest request, List<String> miljoer) {

        var requestXml = marshallToXML(requestContext, request);

        var miljoerResponse = endringsmeldingConsumer.sendMessage(requestXml, miljoer);

        return miljoerResponse.entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, entry -> {

                    try {
                        return getResponseStatus(TpsMeldingCommand.NO_RESPONSE.equals(entry.getValue()) ? null :
                                (KontaktopplysningerResponse) unmarshallFromXml(responseContext, entry.getValue()));

                    } catch (JAXBException e) {
                        log.error(e.getMessage(), e);
                        return getErrorStatus(e);
                    }
                }));
    }
}
