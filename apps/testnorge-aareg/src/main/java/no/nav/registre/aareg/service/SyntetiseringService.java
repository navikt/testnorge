package no.nav.registre.aareg.service;

import static java.util.stream.Collectors.toCollection;
import static no.nav.registre.aareg.consumer.ws.AaregWsConsumer.STATUS_OK;
import static no.nav.registre.aareg.util.ArbeidsforholdMappingUtil.getLocalDateTimeFromLocalDate;
import static no.nav.registre.aareg.util.ArbeidsforholdMappingUtil.mapArbeidsforholdToRsArbeidsforhold;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.aareg.AaregSaveInHodejegerenRequest;
import no.nav.registre.aareg.IdentMedData;
import no.nav.registre.aareg.consumer.rs.AaregSyntetisererenConsumer;
import no.nav.registre.aareg.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.aareg.consumer.rs.KodeverkConsumer;
import no.nav.registre.aareg.consumer.rs.response.KodeverkResponse;
import no.nav.registre.aareg.consumer.ws.request.RsAaregOppdaterRequest;
import no.nav.registre.aareg.consumer.ws.request.RsAaregOpprettRequest;
import no.nav.registre.aareg.domain.RsArbeidsavtale;
import no.nav.registre.aareg.domain.RsArbeidsforhold;
import no.nav.registre.aareg.domain.RsOrganisasjon;
import no.nav.registre.aareg.domain.RsPeriode;
import no.nav.registre.aareg.domain.RsPersonAareg;
import no.nav.registre.aareg.provider.rs.requests.SyntetiserAaregRequest;
import no.nav.registre.aareg.provider.rs.response.RsAaregResponse;
import no.nav.registre.aareg.syntetisering.RsAaregSyntetiseringsRequest;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Arbeidsforhold;

@Service
@Slf4j
@RequiredArgsConstructor
public class SyntetiseringService {

    private static final String AAREG_NAME = "aareg";
    private static final int MINIMUM_ALDER = 13;
    private static final double NYTT_ARBEIDSFORHOLD_SANNSYNLIGHET = 0.1;

    private final HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;
    private final HodejegerenConsumer hodejegerenConsumer;
    private final AaregSyntetisererenConsumer aaregSyntetisererenConsumer;
    private final AaregService aaregService;
    private final KodeverkConsumer kodeverkConsumer;
    private final Random rand = new Random();

    public ResponseEntity<List<RsAaregResponse>> opprettArbeidshistorikkOgSendTilAaregstub(
            SyntetiserAaregRequest syntetiserAaregRequest,
            Boolean sendAlleEksisterende
    ) {
        List<RsAaregResponse> statusFraAareg = new ArrayList<>();
        var levendeIdenter = new HashSet<>(hentLevendeIdenter(syntetiserAaregRequest.getAvspillergruppeId(), MINIMUM_ALDER));
        var identerIAaregstub = new ArrayList<>(hentIdenterIAvspillergruppeMedArbeidsforhold(syntetiserAaregRequest.getAvspillergruppeId(), syntetiserAaregRequest.getMiljoe(), false));
        if (sendAlleEksisterende != null && sendAlleEksisterende) {
            Map<String, List<Arbeidsforhold>> identerSomSkalBeholdeArbeidsforhold = new HashMap<>();
            populerIdenterSomSkalBeholdeArbeidsforhold(syntetiserAaregRequest, identerIAaregstub, identerSomSkalBeholdeArbeidsforhold);
            levendeIdenter.removeAll(identerSomSkalBeholdeArbeidsforhold.keySet());
            statusFraAareg.add(oppdaterArbeidsforholdPaaEksisterendeIdenter(identerSomSkalBeholdeArbeidsforhold, syntetiserAaregRequest.getMiljoe()));
        } else {
            levendeIdenter.removeAll(identerIAaregstub);
        }

        int antallNyeIdenter = syntetiserAaregRequest.getAntallNyeIdenter();
        if (antallNyeIdenter > levendeIdenter.size()) {
            antallNyeIdenter = levendeIdenter.size();
            log.info("Fant ikke nok ledige identer i avspillergruppe. Lager arbeidsforhold på {} identer.", antallNyeIdenter);
        }

        List<String> utvalgteIdenter = new ArrayList<>(levendeIdenter);
        Collections.shuffle(utvalgteIdenter);
        utvalgteIdenter = utvalgteIdenter.subList(0, antallNyeIdenter);

        var syntetiserteArbeidsforhold = aaregSyntetisererenConsumer.getSyntetiserteArbeidsforholdsmeldinger(utvalgteIdenter);
        validerArbeidsforholdMotAaregSpecs(syntetiserteArbeidsforhold);
        for (var opprettRequest : syntetiserteArbeidsforhold) {
            opprettRequest.setEnvironments(Collections.singletonList(syntetiserAaregRequest.getMiljoe()));
            RsAaregOpprettRequest rsAaregOpprettRequest = mapSyntetiseringsRequestToOpprettRequest(opprettRequest);
            var response = aaregService.opprettArbeidsforhold(rsAaregOpprettRequest);

            if (response != null) {
                if (STATUS_OK.equals(response.getStatusPerMiljoe().get(syntetiserAaregRequest.getMiljoe()))) {
                    lagreArbeidsforholdIHodejegeren(rsAaregOpprettRequest);
                } else {
                    log.error("Kunne ikke opprette arbeidsforhold: {}", response.getStatusPerMiljoe().get(syntetiserAaregRequest.getMiljoe()));
                }
                statusFraAareg.add(response);
            }
        }

        return ResponseEntity.ok().body(statusFraAareg);
    }

    public List<RsAaregResponse> sendArbeidsforholdTilAareg(
            List<RsAaregSyntetiseringsRequest> arbeidsforhold,
            boolean fyllUtArbeidsforhold
    ) {
        List<RsAaregResponse> aaregResponses = new ArrayList<>(arbeidsforhold.size());
        if (fyllUtArbeidsforhold) {
            List<String> identer = arbeidsforhold.stream().map(x -> x.getArbeidsforhold().getArbeidstaker().getIdent()).collect(Collectors.toList());

            var syntetiserteArbeidsforhold = aaregSyntetisererenConsumer.getSyntetiserteArbeidsforholdsmeldinger(identer);

            for (int i = 0; i < arbeidsforhold.size() && i < syntetiserteArbeidsforhold.size(); i++) {
                var syntetisertArbeidsforhold = syntetiserteArbeidsforhold.get(i).getArbeidsforhold();
                var originaltArbeidsforhold = arbeidsforhold.get(i).getArbeidsforhold();

                BeanWrapper original = new BeanWrapperImpl(originaltArbeidsforhold);
                BeanWrapper synt = new BeanWrapperImpl(syntetisertArbeidsforhold);
                PropertyDescriptor[] syntPropertyDescriptors = synt.getPropertyDescriptors();

                for (PropertyDescriptor pd : syntPropertyDescriptors) {
                    String pdName = pd.getName();
                    Object originalPropertyValue = original.getPropertyValue(pdName);
                    Object syntPropertyValue = synt.getPropertyValue(pdName);
                    if (originalPropertyValue == null && syntPropertyValue != null && !"class".equals(pdName)) {
                        original.setPropertyValue(pd.getName(), syntPropertyValue);
                    }
                }
            }

            validerArbeidsforholdMotAaregSpecs(arbeidsforhold);

            List<RsAaregSyntetiseringsRequest> ugyldigeArbeidsforhold = new ArrayList<>();

            sjekkArbeidsforholdEtterArbeidsavtale(arbeidsforhold, ugyldigeArbeidsforhold);

            arbeidsforhold.removeAll(ugyldigeArbeidsforhold);
        }

        for (var forholdet : arbeidsforhold) {
            var opprettRequest = mapSyntetiseringsRequestToOpprettRequest(forholdet);
            var aaregResponse = aaregService.opprettArbeidsforhold(opprettRequest);
            aaregResponses.add(aaregResponse);
        }

        return aaregResponses;
    }

    private void sjekkArbeidsforholdEtterArbeidsavtale(List<RsAaregSyntetiseringsRequest> arbeidsforhold, List<RsAaregSyntetiseringsRequest> ugyldigeArbeidsforhold) {
        for (var arbeidsforholdsResponse : arbeidsforhold) {
            if (arbeidsforholdsResponse.getArbeidsforhold().getArbeidsavtale() == null) {
                log.warn("Arbeidsavtale er null for arbeidstaker med id {}. Hopper over opprettelse.", arbeidsforholdsResponse.getArbeidsforhold().getArbeidstaker().getIdent());
                ugyldigeArbeidsforhold.add(arbeidsforholdsResponse);
            }
        }
    }

    public Set<String> hentIdenterIAvspillergruppeMedArbeidsforhold(
            Long avspillergruppeId,
            String miljoe,
            boolean validerMotAareg
    ) {
        var identerIAvspillergruppe = new HashSet<>(hodejegerenConsumer.get(avspillergruppeId));

        if (validerMotAareg) {
            var iterator = identerIAvspillergruppe.iterator();
            while (iterator.hasNext()) {
                var ident = iterator.next();
                try {
                    var response = aaregService.hentArbeidsforhold(ident, miljoe);
                    if (response.hasBody()) {
                        var responseBody = response.getBody();
                        if (responseBody == null || responseBody.isEmpty()) {
                            iterator.remove();
                        }
                    } else {
                        iterator.remove();
                    }
                } catch (HttpStatusCodeException e) {
                    iterator.remove();
                }
            }
        }

        return identerIAvspillergruppe;
    }

    private RsAaregResponse oppdaterArbeidsforholdPaaEksisterendeIdenter(
            Map<String, List<Arbeidsforhold>> identerSomSkalBeholdeArbeidsforhold,
            String miljoe
    ) {
        Map<String, String> aaregResponses = new HashMap<>();
        for (var arbeidsforholdliste : identerSomSkalBeholdeArbeidsforhold.values()) {
            for (var arbeidsforhold : arbeidsforholdliste) {
                var oppdaterRequest = mapAaregResponseToOppdateringsRequest(arbeidsforhold);
                oppdaterRequest.setEnvironments(Collections.singletonList(miljoe));
                oppdaterRequest.setRapporteringsperiode(LocalDateTime.now());
                aaregResponses.putAll(aaregService.oppdaterArbeidsforhold(oppdaterRequest));
            }
        }
        log.info("Status på oppdatering: {}", aaregResponses);
        return RsAaregResponse.builder().statusPerMiljoe(aaregResponses).build();
    }

    private RsAaregOppdaterRequest mapAaregResponseToOppdateringsRequest(Arbeidsforhold aaregResponse) {
        var arbeidsforhold = mapArbeidsforholdToRsArbeidsforhold(aaregResponse);
        var oppdaterRequest = new RsAaregOppdaterRequest();
        oppdaterRequest.setArbeidsforhold(arbeidsforhold);
        return oppdaterRequest;
    }

    private void populerIdenterSomSkalBeholdeArbeidsforhold(
            SyntetiserAaregRequest syntetiserAaregRequest,
            List<String> identerIAaregstub,
            Map<String, List<Arbeidsforhold>> identerMedArbeidsforholdFraAareg
    ) {
        Collections.shuffle(identerIAaregstub);
        var identerSomSkalBeholdeArbeidsforhold = identerIAaregstub.stream()
                .limit(Math.round(identerIAaregstub.size() * (1 - NYTT_ARBEIDSFORHOLD_SANNSYNLIGHET)))
                .collect(toCollection(LinkedHashSet::new));
        for (var ident : identerSomSkalBeholdeArbeidsforhold) {
            var aaregResponse = aaregService.hentArbeidsforhold(ident, syntetiserAaregRequest.getMiljoe());
            if (aaregResponse.getStatusCode().is2xxSuccessful() && aaregResponse.hasBody()) {
                identerMedArbeidsforholdFraAareg.put(ident, aaregResponse.getBody());
            }
        }
    }

    private void lagreArbeidsforholdIHodejegeren(RsAaregOpprettRequest opprettRequest) {
        var identMedData = new IdentMedData(opprettRequest.getArbeidsforhold().getArbeidstaker().getIdent(), Collections.singletonList(opprettRequest.getArbeidsforhold()));
        var hodejegerenRequest = new AaregSaveInHodejegerenRequest(AAREG_NAME, Collections.singletonList(identMedData));
        hodejegerenHistorikkConsumer.saveHistory(hodejegerenRequest);
    }

    private void validerArbeidsforholdMotAaregSpecs(List<RsAaregSyntetiseringsRequest> arbeidsforholdRequests) {
        KodeverkResponse yrkeskoder = kodeverkConsumer.getYrkeskoder();
        for (var request : arbeidsforholdRequests) {
            var arbeidsforhold = request.getArbeidsforhold();
            if (arbeidsforhold.getArbeidsavtale().getSisteLoennsendringsdato() != null) {
                arbeidsforhold.getArbeidsavtale().setSisteLoennsendringsdato(null);
            }

            if (Math.abs(arbeidsforhold.getArbeidsavtale().getAvtaltArbeidstimerPerUke() - 0.0) < 0.001) {
                if (Math.abs(arbeidsforhold.getArbeidsavtale().getStillingsprosent() - 0.0) < 0.001) {
                    arbeidsforhold.getArbeidsavtale().setStillingsprosent(100.0);
                    arbeidsforhold.getArbeidsavtale().setAvtaltArbeidstimerPerUke(40.0);
                } else {
                    arbeidsforhold.getArbeidsavtale().setAvtaltArbeidstimerPerUke((arbeidsforhold.getArbeidsavtale().getStillingsprosent() / 100.0) * 40.0);
                }
            }

            if (arbeidsforhold.getArbeidsavtale().getYrke().length() != 7) {
                arbeidsforhold.getArbeidsavtale().setYrke(yrkeskoder.getKoder().get(rand.nextInt(yrkeskoder.getKoder().size())));
            }
        }
    }

    private RsAaregOpprettRequest mapSyntetiseringsRequestToOpprettRequest(RsAaregSyntetiseringsRequest syntetiseringsRequest) {
        var arbeidsforhold = RsArbeidsforhold.builder()
                .arbeidsforholdID(syntetiseringsRequest.getArbeidsforhold().getArbeidsforholdID())
                .arbeidsgiver(RsOrganisasjon.builder()
                        .orgnummer(((RsOrganisasjon) syntetiseringsRequest.getArbeidsforhold().getArbeidsgiver()).getOrgnummer())
                        .build())
                .arbeidsforholdstype(syntetiseringsRequest.getArbeidsforhold().getArbeidsforholdstype())
                .arbeidstaker(RsPersonAareg.builder()
                        .ident(syntetiseringsRequest.getArbeidsforhold().getArbeidstaker().getIdent())
                        .identtype(syntetiseringsRequest.getArbeidsforhold().getArbeidstaker().getIdenttype())
                        .build())
                .ansettelsesPeriode(RsPeriode.builder()
                        .fom(getLocalDateTimeFromLocalDate(syntetiseringsRequest.getArbeidsforhold().getAnsettelsesPeriode().getFom()))
                        .tom(getLocalDateTimeFromLocalDate(syntetiseringsRequest.getArbeidsforhold().getAnsettelsesPeriode().getTom()))
                        .build())
                .arbeidsavtale(RsArbeidsavtale.builder()
                        .arbeidstidsordning(syntetiseringsRequest.getArbeidsforhold().getArbeidsavtale().getArbeidstidsordning())
                        .avtaltArbeidstimerPerUke(syntetiseringsRequest.getArbeidsforhold().getArbeidsavtale().getAvtaltArbeidstimerPerUke())
                        .endringsdatoStillingsprosent(getLocalDateTimeFromLocalDate(syntetiseringsRequest.getArbeidsforhold().getArbeidsavtale().getEndringsdatoStillingsprosent()))
                        .sisteLoennsendringsdato(getLocalDateTimeFromLocalDate(syntetiseringsRequest.getArbeidsforhold().getArbeidsavtale().getSisteLoennsendringsdato()))
                        .stillingsprosent(syntetiseringsRequest.getArbeidsforhold().getArbeidsavtale().getStillingsprosent())
                        .yrke(syntetiseringsRequest.getArbeidsforhold().getArbeidsavtale().getYrke())
                        .build())
                .build();
        return RsAaregOpprettRequest.builder()
                .arbeidsforhold(arbeidsforhold)
                .environments(syntetiseringsRequest.getEnvironments())
                .arkivreferanse(syntetiseringsRequest.getArkivreferanse())
                .build();
    }

    @Timed(value = "aareg.resource.latency", extraTags = {"operation", "hodejegeren"})
    private List<String> hentLevendeIdenter(
            Long avspillergruppeId,
            int minimumAlder
    ) {
        return hodejegerenConsumer.getLevende(avspillergruppeId, minimumAlder);
    }
}
