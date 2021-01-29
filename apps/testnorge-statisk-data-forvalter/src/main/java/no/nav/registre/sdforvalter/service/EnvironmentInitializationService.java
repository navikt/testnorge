package no.nav.registre.sdforvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.sdforvalter.adapter.AaregAdapter;
import no.nav.registre.sdforvalter.adapter.EregAdapter;
import no.nav.registre.sdforvalter.adapter.KrrAdapter;
import no.nav.registre.sdforvalter.consumer.rs.AaregConsumer;
import no.nav.registre.sdforvalter.consumer.rs.EregMapperConsumer;
import no.nav.registre.sdforvalter.consumer.rs.KrrConsumer;
import no.nav.registre.sdforvalter.domain.Ereg;
import no.nav.registre.sdforvalter.domain.EregListe;
import no.nav.registre.sdforvalter.domain.status.ereg.OrganisasjonStatusMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnvironmentInitializationService {

    private final AaregConsumer aaregConsumer;
    private final KrrConsumer krrConsumer;
    private final EregMapperConsumer eregMapperConsumer;
    private final EregStatusService eregStatusService;

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

    public void opprettOrgViaKafka(String environment, String gruppe) {
        EregListe orgMedJuridiskEnhetListe = eregAdapter.fetchBy(gruppe);



        //Alt under her legges i egen fil
        List<OrganisasjonNaermereAvro> orgMedUnderenhetListe = new ArrayList<>();
        orgMedJuridiskEnhetListe.stream()
                .forEach(org -> {
                    if (finnOrgIListe(orgMedUnderenhetListe, org.getOrgnr()) == null) {
                        orgMedUnderenhetListe.add(lagOrganisasjon(org, null));
                    }

                    String juridiskEnhetOrgnr = org.getJuridiskEnhet();
                    if (juridiskEnhetOrgnr != null) {
                        OrganisasjonNaermereAvro organisasjon = finnOrgIListe(orgMedUnderenhetListe, org.getOrgnr());
                        OrganisasjonNaermereAvro juridiskEnhet = finnOrgIListe(orgMedUnderenhetListe, juridiskEnhetOrgnr);
                        if (juridiskEnhet == null) {
                            OrganisasjonNaermereAvro e = lagOrganisasjon(finnEregIListe(orgMedJuridiskEnhetListe, juridiskEnhetOrgnr), organisasjon);
                            orgMedUnderenhetListe.add(e);
                        } else {
                            juridiskEnhet.addUnderenhet(organisasjon);
                        }
                    }
                });

        //send videre til OrganisasjonMottakConsumer og gjør om til faktisk avroskjema
    }

    public OrganisasjonNaermereAvro lagOrganisasjon(Ereg org, OrganisasjonNaermereAvro underenhet) {
        //Opprett organisasjon med underenhet
        var nyOrganisasjon = new OrganisasjonNaermereAvro(org);
        if (underenhet != null) {
            nyOrganisasjon.addUnderenhet(underenhet);
        }
        return nyOrganisasjon;
    }

    public OrganisasjonNaermereAvro finnOrgIListe(List<OrganisasjonNaermereAvro> orgliste, String orgnr) {
        List<OrganisasjonNaermereAvro> organisasjon = orgliste.stream().filter(org -> org.getOrgnummer().equals(orgnr)).collect(Collectors.toList());
        if (!organisasjon.isEmpty()) {
            return organisasjon.get(0);
        }
        return null;
    }

    public Ereg finnEregIListe(EregListe orgliste, String orgnummer) {
        //Mangler utvei hvis den juridiske enheten som er oppgitt ikke er med i lista. Stoppe opprettelse eller lage standard?
        var eregliste = orgliste.stream().filter(org -> org.getOrgnr().equals(orgnummer)).collect(Collectors.toList());
        if (!eregliste.isEmpty()) {
            return eregliste.get(0);
        }
        return null;
    }

    public void updateEregByOrgnr(String environment, String orgnr) {
        log.info("Oppdater {} i {} Ereg...", orgnr, environment);
        OrganisasjonStatusMap status = eregStatusService.getStatusByOrgnr(environment, orgnr, false);
        if (status.getMap().isEmpty()) {
            log.info("Fant ingen endringer i for {} for {} Ereg", orgnr, environment);
        } else {
            eregMapperConsumer.update(eregAdapter.fetchByOrgnr(orgnr), environment);
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
            eregMapperConsumer.update(eregAdapter.fetchByIds(status.getMap().keySet()), environment);
            log.info("Oppdatering er ferdig.");
        }

    }

    public void initializeKrr(String gruppe) {
        log.info("Start init av KRR ...");
        krrConsumer.send(krrAdapter.fetchBy(gruppe));
        log.info("Init av krr er ferdig.");
    }
}