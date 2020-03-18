package no.nav.registre.sdForvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import no.nav.registre.sdForvalter.adapter.AaregAdapter;
import no.nav.registre.sdForvalter.adapter.EregAdapter;
import no.nav.registre.sdForvalter.adapter.KrrAdapter;
import no.nav.registre.sdForvalter.consumer.rs.AaregConsumer;
import no.nav.registre.sdForvalter.consumer.rs.EregMapperConsumer;
import no.nav.registre.sdForvalter.consumer.rs.KrrConsumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnvironmentInitializationService {

    private final AaregConsumer aaregConsumer;
    private final KrrConsumer krrConsumer;
    private final EregMapperConsumer eregMapperConsumer;

    private final EregAdapter eregAdapter;
    private final KrrAdapter krrAdapter;
    private final AaregAdapter aaregAdapter;

    private final IdentService identService;

    @Value("${tps.statisk.avspillergruppeId}")
    private Long staticDataPlaygroup;

    public void initializeEnvironmentWithStaticData(String environment, String gruppe) {
        log.info("Start init of all static data sets...");
        initializeIdent(environment, gruppe);
        initializeKrr(gruppe);
        initializeAareg(environment, gruppe);
        initializeEreg(environment, gruppe);
        log.info("Completed init of all static data sets.");
    }


    public void initializeIdent(String environment, String gruppe) {
        log.info("Start init av identer...");
        identService.send(environment, gruppe);
        log.info("Init av identer er ferdig.");
    }

    public void initializeAareg(String environment, String gruppe) {
        log.info("Start init av Aareg...");
        aaregConsumer.sendArbeidsforhold(aaregAdapter.fetchBy(gruppe), environment);
        log.info("Init av Aareg eer ferdig.");
    }

    public void initializeEreg(String environment, String gruppe) {
        log.info("Start init av Ereg ...");
        eregMapperConsumer.create(eregAdapter.fetchBy(gruppe), environment);
        log.info("Init of Ereg er ferdig.");
    }

    public void initializeKrr(String gruppe) {
        log.info("Start init av KRR ...");
        krrConsumer.send(krrAdapter.fetchBy(gruppe));
        log.info("Init av krr er ferdig.");
    }

}
