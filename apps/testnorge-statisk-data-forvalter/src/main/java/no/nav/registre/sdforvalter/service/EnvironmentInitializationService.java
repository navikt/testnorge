package no.nav.registre.sdforvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import no.nav.registre.sdforvalter.adapter.AaregAdapter;
import no.nav.registre.sdforvalter.adapter.EregAdapter;
import no.nav.registre.sdforvalter.adapter.KrrAdapter;
import no.nav.registre.sdforvalter.consumer.rs.AaregConsumer;
import no.nav.registre.sdforvalter.consumer.rs.OrganisasjonMottakServiceConsumer;
import no.nav.registre.sdforvalter.consumer.rs.KrrConsumer;
import no.nav.registre.sdforvalter.domain.Ereg;
import no.nav.registre.sdforvalter.domain.status.ereg.OrganisasjonStatusMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnvironmentInitializationService {

    private final AaregConsumer aaregConsumer;
    private final KrrConsumer krrConsumer;
    private final OrganisasjonMottakServiceConsumer organisasjonMottakServiceConsumer;
    private final EregStatusService eregStatusService;

    private final EregAdapter eregAdapter;
    private final KrrAdapter krrAdapter;
    private final AaregAdapter aaregAdapter;
    private final IdentService identService;


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
        organisasjonMottakServiceConsumer.create(eregAdapter.fetchBy(gruppe), environment);
        log.info("Init of Ereg er ferdig.");
    }

    public void updateEregByOrgnr(String environment, String orgnr) {
        log.info("Oppdater {} i {} Ereg...", orgnr, environment);
        OrganisasjonStatusMap status = eregStatusService.getStatusByOrgnr(environment, orgnr, false);
        if(status.getMap().isEmpty()){
            log.info("Fant ingen endringer i for {} for {} Ereg", orgnr, environment);
        } else {
            organisasjonMottakServiceConsumer.update(eregAdapter.fetchByOrgnr(orgnr), environment);
            log.info("Oppdatering er ferdig.");
        }
    }

    public void updateEregByGruppe(String environment, String gruppe) {
        log.info("Oppdater {} gruppen i {} Ereg...", gruppe, environment);
        OrganisasjonStatusMap status = eregStatusService.getStatusByGruppe(environment, gruppe, false);
        if (status.getMap().isEmpty()) {
            log.info("Fant ingen endringer i gruppen {} for {} Ereg", gruppe, environment);
        } else {
            log.info("Oppdaterer {} organisasjoner.", status.getMap().size());
            organisasjonMottakServiceConsumer.update(eregAdapter.fetchByIds(status.getMap().keySet()), environment);
            log.info("Oppdatering er ferdig.");
        }

    }

    public void opprett(String environment, String orgnummer) {
        log.info("Oppretter org {} i miljo {}.", orgnummer, environment);
        Ereg ereg = eregAdapter.fetchByOrgnr(orgnummer);
        organisasjonMottakServiceConsumer.create(ereg, environment);
    }

    public void initializeKrr(String gruppe) {
        log.info("Start init av KRR ...");
        krrConsumer.send(krrAdapter.fetchBy(gruppe));
        log.info("Init av krr er ferdig.");
    }
}