package no.nav.testnav.apps.syntaaregservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.testnav.apps.syntaaregservice.consumer.KodeverkConsumer;
import no.nav.testnav.apps.syntaaregservice.consumer.SyntAaregConsumer;
import no.nav.testnav.apps.syntaaregservice.consumer.request.RsAaregOpprettRequest;
import no.nav.testnav.apps.syntaaregservice.domain.aareg.*;
import no.nav.testnav.apps.syntaaregservice.domain.synt.RsAaregSyntetiseringsRequest;
import no.nav.testnav.apps.syntaaregservice.provider.response.RsAaregResponse;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.util.*;

import static no.nav.testnav.apps.syntaaregservice.util.ArbeidsforholdMappingUtil.getLocalDateTimeFromLocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class SyntetiseringService {

    private final SyntAaregConsumer aaregSyntetisererenConsumer;
    private final AaregService aaregService;
    private final KodeverkConsumer kodeverkConsumer;
    private final Random rand = new Random();

    public List<RsAaregResponse> sendArbeidsforholdTilAareg(
            List<RsAaregSyntetiseringsRequest> arbeidsforhold,
            boolean fyllUtArbeidsforhold
    ) {
        List<RsAaregResponse> aaregResponses = new ArrayList<>(arbeidsforhold.size());
        if (fyllUtArbeidsforhold) {
            List<String> identer = arbeidsforhold.stream().map(x -> x.getArbeidsforhold().getArbeidstaker().getIdent()).toList();

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

    private void validerArbeidsforholdMotAaregSpecs(List<RsAaregSyntetiseringsRequest> arbeidsforholdRequests) {
        var yrkeskoder = kodeverkConsumer.getYrkeskoder();
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
}
