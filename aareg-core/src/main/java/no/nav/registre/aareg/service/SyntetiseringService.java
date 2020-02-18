package no.nav.registre.aareg.service;

import static no.nav.registre.aareg.consumer.ws.AaregWsConsumer.STATUS_OK;
import static no.nav.registre.aareg.service.AaregAbstractClient.getArbeidsgiver;
import static no.nav.registre.aareg.service.AaregAbstractClient.getEndringsdatoStillingsprosent;
import static no.nav.registre.aareg.service.AaregAbstractClient.getPeriodeFom;
import static no.nav.registre.aareg.service.AaregAbstractClient.getPeriodeTom;
import static no.nav.registre.aareg.service.AaregAbstractClient.getSisteLoennsendringsdato;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.aareg.AaregSaveInHodejegerenRequest;
import no.nav.registre.aareg.IdentMedData;
import no.nav.registre.aareg.consumer.rs.AaregSyntetisererenConsumer;
import no.nav.registre.aareg.consumer.rs.AaregstubConsumer;
import no.nav.registre.aareg.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.aareg.consumer.rs.KodeverkConsumer;
import no.nav.registre.aareg.consumer.rs.responses.KodeverkResponse;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class SyntetiseringService {

    private static final String AAREG_NAME = "aareg";
    private static final int MINIMUM_ALDER = 13;

    private final HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;
    private final HodejegerenConsumer hodejegerenConsumer;
    private final AaregSyntetisererenConsumer aaregSyntetisererenConsumer;
    private final AaregstubConsumer aaregstubConsumer;
    private final AaregService aaregService;
    private final KodeverkConsumer kodeverkConsumer;
    private final Random rand;

    public ResponseEntity opprettArbeidshistorikkOgSendTilAaregstub(
            SyntetiserAaregRequest syntetiserAaregRequest,
            Boolean sendAlleEksisterende
    ) {
        Set<String> levendeIdenter = new HashSet<>(hentLevendeIdenter(syntetiserAaregRequest.getAvspillergruppeId(), MINIMUM_ALDER));
        Set<String> nyeIdenter = new HashSet<>(syntetiserAaregRequest.getAntallNyeIdenter());
        Set<String> identerIAaregstub = new HashSet<>();
        if (sendAlleEksisterende) {
            identerIAaregstub.addAll(aaregstubConsumer.hentEksisterendeIdenter());
        }
        levendeIdenter.removeAll(identerIAaregstub);
        List<String> utvalgteIdenter = new ArrayList<>(levendeIdenter);

        int antallNyeIdenter = syntetiserAaregRequest.getAntallNyeIdenter();
        if (antallNyeIdenter > utvalgteIdenter.size()) {
            antallNyeIdenter = utvalgteIdenter.size();
            log.info("Fant ikke nok ledige identer i avspillergruppe. Lager arbeidsforhold på {} identer.", antallNyeIdenter);
        }

        for (int i = 0; i < antallNyeIdenter; i++) {
            nyeIdenter.add(utvalgteIdenter.remove(rand.nextInt(utvalgteIdenter.size())));
        }

        identerIAaregstub.addAll(nyeIdenter);
        List<String> lagredeIdenter = new ArrayList<>();
        var syntetiserteArbeidsforhold = aaregSyntetisererenConsumer.getSyntetiserteArbeidsforholdsmeldinger(new ArrayList<>(identerIAaregstub));
        for (var opprettRequest : syntetiserteArbeidsforhold) {
            RsAaregOpprettRequest rsAaregOpprettRequest = mapSyntetiseringsRequestToOpprettRequest(opprettRequest);
            opprettRequest.setEnvironments(Collections.singletonList(syntetiserAaregRequest.getMiljoe()));
            var response = aaregService.opprettArbeidsforhold(rsAaregOpprettRequest);

            if (response != null) {
                if (STATUS_OK.equals(response.getStatusPerMiljoe().get(syntetiserAaregRequest.getMiljoe()))) {
                    lagredeIdenter.add(opprettRequest.getArbeidsforhold().getArbeidstaker().getIdent());
                    aaregstubConsumer.sendTilAaregstub(Collections.singletonList(rsAaregOpprettRequest));
                    lagreArbeidsforholdIHodejegeren(rsAaregOpprettRequest);
                } else {
                    log.error("Kunne ikke opprette arbeidsforhold: {}", response.getStatusPerMiljoe().get(syntetiserAaregRequest.getMiljoe()));
                }
            }
        }

        StringBuilder statusFraAareg = new StringBuilder();

        if (!CollectionUtils.isEmpty(lagredeIdenter)) {
            statusFraAareg
                    .append("Identer som ble lagret i aareg: ")
                    .append(lagredeIdenter)
                    .append(". ");
        }

        if (!statusFraAareg.toString().isEmpty()) {
            log.info(statusFraAareg.toString());
        }

        return ResponseEntity.ok().body(statusFraAareg.toString());
    }

    private void lagreArbeidsforholdIHodejegeren(RsAaregOpprettRequest opprettRequest) {
        var identMedData = new IdentMedData(opprettRequest.getArbeidsforhold().getArbeidstaker().getIdent(), Collections.singletonList(opprettRequest.getArbeidsforhold()));
        var hodejegerenRequest = new AaregSaveInHodejegerenRequest(AAREG_NAME, Collections.singletonList(identMedData));
        hodejegerenHistorikkConsumer.saveHistory(hodejegerenRequest);
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

            for (var arbeidsforholdsResponse : arbeidsforhold) {
                if (arbeidsforholdsResponse.getArbeidsforhold().getArbeidsavtale() == null) {
                    log.warn("Arbeidsavtale er null for arbeidstaker med id {}. Hopper over opprettelse.", arbeidsforholdsResponse.getArbeidsforhold().getArbeidstaker().getIdent());
                    ugyldigeArbeidsforhold.add(arbeidsforholdsResponse);
                }
            }

            arbeidsforhold.removeAll(ugyldigeArbeidsforhold);
        }

        for (var forholdet : arbeidsforhold) {
            RsAaregOpprettRequest opprettRequest = mapSyntetiseringsRequestToOpprettRequest(forholdet);
            RsAaregResponse aaregResponse = aaregService.opprettArbeidsforhold(opprettRequest);
            aaregResponses.add(aaregResponse);
        }

        return aaregResponses;
    }

    private void validerArbeidsforholdMotAaregSpecs(List<RsAaregSyntetiseringsRequest> arbeidsforholdRequests) {
        KodeverkResponse yrkeskoder = kodeverkConsumer.getYrkeskoder();
        for (var request : arbeidsforholdRequests) {
            var arbeidsforhold = request.getArbeidsforhold();
            if (arbeidsforhold.getArbeidsavtale().getSisteLoennsendringsdato() != null) {
                arbeidsforhold.getArbeidsavtale().setSisteLoennsendringsdato(null);
            }

            if (arbeidsforhold.getArbeidsavtale().getAvtaltArbeidstimerPerUke() == 0.0) {
                if (arbeidsforhold.getArbeidsavtale().getStillingsprosent() == 0.0) {
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
                        .orgnummer(getArbeidsgiver(syntetiseringsRequest.getArbeidsforhold()).getOrgnummer())
                        .build())
                .arbeidsforholdstype(syntetiseringsRequest.getArbeidsforhold().getArbeidsforholdstype())
                .arbeidstaker(RsPersonAareg.builder()
                        .ident(syntetiseringsRequest.getArbeidsforhold().getArbeidstaker().getIdent())
                        .identtype(syntetiseringsRequest.getArbeidsforhold().getArbeidstaker().getIdenttype())
                        .build())
                .ansettelsesPeriode(RsPeriode.builder()
                        .fom(getPeriodeFom(syntetiseringsRequest.getArbeidsforhold()))
                        .tom(getPeriodeTom(syntetiseringsRequest.getArbeidsforhold()))
                        .build())
                .arbeidsavtale(RsArbeidsavtale.builder()
                        .arbeidstidsordning(syntetiseringsRequest.getArbeidsforhold().getArbeidsavtale().getArbeidstidsordning())
                        .avtaltArbeidstimerPerUke(syntetiseringsRequest.getArbeidsforhold().getArbeidsavtale().getAvtaltArbeidstimerPerUke())
                        .endringsdatoStillingsprosent(getEndringsdatoStillingsprosent(syntetiseringsRequest.getArbeidsforhold()))
                        .sisteLoennsendringsdato(getSisteLoennsendringsdato(syntetiseringsRequest.getArbeidsforhold()))
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

    @Timed(value = "aareg.resource.latency", extraTags = { "operation", "hodejegeren" })
    private List<String> hentLevendeIdenter(
            Long avspillergruppeId,
            int minimumAlder
    ) {
        return hodejegerenConsumer.getLevende(avspillergruppeId, minimumAlder);
    }
}
