package no.nav.registre.sdforvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.consumer.rs.aareg.AaregConsumer;
import no.nav.registre.sdforvalter.consumer.rs.kodeverk.KodeverkConsumer;
import no.nav.registre.sdforvalter.consumer.rs.aareg.SyntAaregConsumer;
import no.nav.registre.sdforvalter.consumer.rs.aareg.response.ArbeidsforholdRespons;
import no.nav.registre.sdforvalter.consumer.rs.aareg.request.RsAaregSyntetiseringsRequest;
import no.nav.registre.sdforvalter.consumer.rs.aareg.request.RsOrganisasjon;
import no.nav.registre.sdforvalter.consumer.rs.aareg.request.RsSyntPerson;
import no.nav.registre.sdforvalter.consumer.rs.aareg.request.RsSyntetiskArbeidsforhold;
import no.nav.registre.sdforvalter.domain.Aareg;
import no.nav.registre.sdforvalter.domain.AaregListe;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.dto.aareg.v1.Organisasjon;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
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
        var nyeArbeidsforhold = liste.getListe().stream()
                .filter(item -> arbeidsforholdEksistererIkkeAllerede(
                        aaregConsumer.hentArbeidsforhold(item.getFnr(), environment), item))
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
                            environment);
                })
                .toList();
        return sendArbeidsforholdTilAareg(nyeArbeidsforhold, true);
    }

    private Boolean arbeidsforholdEksistererIkkeAllerede(
            ArbeidsforholdRespons response,
            Aareg request
    ) {
        return response.getEksisterendeArbeidsforhold().stream()
                .noneMatch(res -> isArbeidsgiverOrganisasjonAlike(res, String.valueOf(request.getOrgId())));
    }

    private boolean isArbeidsgiverOrganisasjonAlike(Arbeidsforhold arbeidsforhold, String orgnr) {
        return arbeidsforhold.getArbeidsgiver() instanceof Organisasjon organisasjon &&
                organisasjon.getOrganisasjonsnummer().equals(orgnr);
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
            var aaregResponse = aaregConsumer.opprettArbeidsforhold(opprettRequest, forholdet.getEnvironment());
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

}
