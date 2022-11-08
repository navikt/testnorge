package no.nav.registre.sdforvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.consumer.rs.AaregConsumer;
import no.nav.registre.sdforvalter.consumer.rs.KodeverkConsumer;
import no.nav.registre.sdforvalter.consumer.rs.SyntAaregConsumer;
import no.nav.registre.sdforvalter.consumer.rs.domain.ArbeidsforholdRespons;
import no.nav.registre.sdforvalter.consumer.rs.request.syntetisering.RsAaregSyntetiseringsRequest;
import no.nav.registre.sdforvalter.consumer.rs.request.syntetisering.RsOrganisasjon;
import no.nav.registre.sdforvalter.consumer.rs.request.syntetisering.RsSyntPerson;
import no.nav.registre.sdforvalter.consumer.rs.request.syntetisering.RsSyntetiskArbeidsforhold;
import no.nav.registre.sdforvalter.domain.AaregListe;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


@Service
@Slf4j
@RequiredArgsConstructor
public class AaregService {
    private final SyntAaregConsumer aaregSyntetisererenConsumer;
    private final AaregConsumer aaregConsumer;
    private final KodeverkConsumer kodeverkConsumer;
    private final Random rand = new Random();


    public List<ArbeidsforholdRespons> sendArbeidsforhold(
            AaregListe liste, String environment
    ) {
        List<RsAaregSyntetiseringsRequest> requestList = liste.getListe()
                .stream()
                // TODO filter arbeidsforhold som allerede eksisterer.
                .map(item -> {
                    var arbeidsgiver = RsOrganisasjon.builder()
                            .orgnummer(String.valueOf(item.getOrgId()))
                            .build();
                    var arbeidstaker = RsSyntPerson.builder()
                            .ident(item.getFnr())
                            .identtype("FNR")
                            .build();
                    return new RsAaregSyntetiseringsRequest(
                            RsSyntetiskArbeidsforhold.builder()
                                    .arbeidsgiver(arbeidsgiver)
                                    .arbeidstaker(arbeidstaker)
                                    .build(),
                            null,
                            Collections.singletonList(environment));
                })
                .toList();
        return sendArbeidsforholdTilAareg(requestList, true);
    }


    private List<ArbeidsforholdRespons> sendArbeidsforholdTilAareg(
            List<RsAaregSyntetiseringsRequest> arbeidsforhold,
            boolean fyllUtArbeidsforhold
    ) {
        List<ArbeidsforholdRespons> aaregResponses = new ArrayList<>(arbeidsforhold.size());
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
            var opprettRequest = forholdet.getArbeidsforhold().toArbeidsforhold();
            var miljoeListe = forholdet.getEnvironments();
            for (var miljoe : miljoeListe) {
                var aaregResponse = aaregConsumer.opprettArbeidsforhold(opprettRequest, miljoe);
                aaregResponses.add(aaregResponse);
            }
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

}
