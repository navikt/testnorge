package no.nav.testnav.apps.tpsmessagingservice.service;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.tpsmessagingservice.consumer.EndringsmeldingConsumer;
import no.nav.testnav.apps.tpsmessagingservice.consumer.TestmiljoerServiceConsumer;
import no.nav.testnav.apps.tpsmessagingservice.consumer.command.TpsMeldingCommand;
import no.nav.testnav.apps.tpsmessagingservice.dto.BankkontoUtlandRequest;
import no.nav.testnav.apps.tpsmessagingservice.dto.BankkontoUtlandResponse;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsMeldingResponse;
import no.nav.testnav.libs.dto.kontoregister.v1.BankkontonrUtlandDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.getErrorStatus;
import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.getResponseStatus;
import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.marshallToXML;
import static no.nav.testnav.apps.tpsmessagingservice.utils.EndringsmeldingUtil.unmarshallFromXml;

@Slf4j
@Service
public class BankkontoUtlandService {

    private final MapperFacade mapperFacade;
    private final EndringsmeldingConsumer endringsmeldingConsumer;
    private final TestmiljoerServiceConsumer testmiljoerServiceConsumer;
    private final JAXBContext requestContext;
    private final JAXBContext responseContext;

    public BankkontoUtlandService(MapperFacade mapperFacade, EndringsmeldingConsumer endringsmeldingConsumer,
                                  TestmiljoerServiceConsumer testmiljoerServiceConsumer) throws JAXBException {

        this.mapperFacade = mapperFacade;
        this.endringsmeldingConsumer = endringsmeldingConsumer;
        this.testmiljoerServiceConsumer = testmiljoerServiceConsumer;

        this.requestContext = JAXBContext.newInstance(BankkontoUtlandRequest.class);
        this.responseContext = JAXBContext.newInstance(BankkontoUtlandResponse.class);
    }

    public Mono<Map<String, TpsMeldingResponse>> sendBankkontonrUtland(String ident, BankkontonrUtlandDTO bankkontonr, List<String> miljoer) {

        return oppdaterBankkontonrUtland(ident, bankkontonr, miljoer);
    }

    public Mono<Map<String, TpsMeldingResponse>> opphoerBankkontonrUtland(String ident, List<String> miljoer) {

        return oppdaterBankkontonrUtland(ident, null, miljoer);
    }

    private Mono<Map<String, TpsMeldingResponse>> oppdaterBankkontonrUtland(String ident, BankkontonrUtlandDTO bankkontonr, List<String> miljoer) {

        Mono<List<String>> miljoerMono = isNull(miljoer) ? testmiljoerServiceConsumer.getMiljoer() : Mono.just(miljoer);

        return miljoerMono.flatMap(resolvedMiljoer -> Mono.fromCallable(() -> {
            var context = new MappingContext.Factory().getContext();
            context.setProperty("ident", ident);

            var request = mapperFacade.map(nonNull(bankkontonr) ? bankkontonr : new BankkontonrUtlandDTO(),
                    BankkontoUtlandRequest.class, context);

            var requestXml = marshallToXML(requestContext, request);
            var miljoerResponse = endringsmeldingConsumer.sendMessage(requestXml, resolvedMiljoer);

            return miljoerResponse.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> {

                        try {
                            return getResponseStatus(TpsMeldingCommand.NO_RESPONSE.equals(entry.getValue()) ? null :
                                    (BankkontoUtlandResponse) unmarshallFromXml(responseContext, entry.getValue()));

                        } catch (JAXBException e) {
                            log.error(e.getMessage(), e);
                            return getErrorStatus(e);
                        }
                    }));
        }).subscribeOn(Schedulers.boundedElastic()));
    }
}
