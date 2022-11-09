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
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;


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
                .map(item ->
                        new RsAaregSyntetiseringsRequest(
                                RsSyntetiskArbeidsforhold.builder()
                                        .arbeidsgiver(RsOrganisasjon.builder()
                                                .orgnummer(String.valueOf(item.getOrgId()))
                                                .build())
                                        .arbeidstaker(RsSyntPerson.builder()
                                                .ident(item.getFnr())
                                                .identtype("FNR")
                                                .build())
                                        .build(),
                                null,
                                environment)
                )
                .collect(Collectors.toList());

        var total = liste.getListe().size();
        var antallNye = nyeArbeidsforhold.size();

        if (total != antallNye) {
            log.info("Fant eksisterende arbeidsforhold for {} av {} identer. Fortsetter opprettelse av nye arbeidsforhold for de gjenværenede {} identene.",
                    total - antallNye,
                    total,
                    antallNye
            );
        }

        return sendArbeidsforholdTilAareg(nyeArbeidsforhold);
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
            List<RsAaregSyntetiseringsRequest> arbeidsforhold
    ) {
        List<ArbeidsforholdRespons> aaregResponses = new ArrayList<>(arbeidsforhold.size());
        fyllInnArbeidsforholdMedSyntetiskeData(arbeidsforhold);

        for (var forholdet : arbeidsforhold) {
            var opprettRequest = forholdet.getArbeidsforhold().toArbeidsforhold();
            var aaregResponse = aaregConsumer.opprettArbeidsforhold(opprettRequest, forholdet.getEnvironment());
            aaregResponses.add(aaregResponse);
        }

        return aaregResponses;
    }


    private void fyllInnArbeidsforholdMedSyntetiskeData (List<RsAaregSyntetiseringsRequest> arbeidsforhold){
        if (!arbeidsforhold.isEmpty()) {
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
                    if (originalPropertyValue == null && syntPropertyValue != null && !"class" .equals(pdName)) {
                        original.setPropertyValue(pd.getName(), syntPropertyValue);
                    }
                }
            }

            validerArbeidsforholdMotAaregSpecs(arbeidsforhold);

            List<RsAaregSyntetiseringsRequest> ugyldigeArbeidsforhold = new ArrayList<>();

            sjekkArbeidsforholdEtterArbeidsavtale(arbeidsforhold, ugyldigeArbeidsforhold);

            if (!ugyldigeArbeidsforhold.isEmpty()) {
                arbeidsforhold.removeAll(ugyldigeArbeidsforhold);
            }
        }
    }

    private void sjekkArbeidsforholdEtterArbeidsavtale(List<RsAaregSyntetiseringsRequest> arbeidsforhold, List<RsAaregSyntetiseringsRequest> ugyldigeArbeidsforhold) {
        for (var arbeidsforholdsResponse : arbeidsforhold) {
            if (isNull(arbeidsforholdsResponse.getArbeidsforhold()) || isNull(arbeidsforholdsResponse.getArbeidsforhold().getArbeidsavtale())) {
                log.warn("Arbeidsavtale er null for arbeidstaker med id {}. Hopper over opprettelse.", arbeidsforholdsResponse.getArbeidsforhold().getArbeidstaker().getIdent());
                ugyldigeArbeidsforhold.add(arbeidsforholdsResponse);
            }
        }
    }

    private void validerArbeidsforholdMotAaregSpecs(List<RsAaregSyntetiseringsRequest> arbeidsforholdRequests) {
        var yrkeskoder = kodeverkConsumer.getYrkeskoder();
        for (var request : arbeidsforholdRequests) {
            if (isNull(request.getArbeidsforhold()) || isNull(request.getArbeidsforhold().getArbeidsavtale())) {
                continue;
            }
            var arbeidsavtale = request.getArbeidsforhold().getArbeidsavtale();
            if (nonNull(arbeidsavtale.getSisteLoennsendringsdato())) {
                arbeidsavtale.setSisteLoennsendringsdato(null);
            }

            if (Math.abs(arbeidsavtale.getAvtaltArbeidstimerPerUke() - 0.0) < 0.001) {
                if (Math.abs(arbeidsavtale.getStillingsprosent() - 0.0) < 0.001) {
                    arbeidsavtale.setStillingsprosent(100.0);
                    arbeidsavtale.setAvtaltArbeidstimerPerUke(40.0);
                } else {
                    arbeidsavtale.setAvtaltArbeidstimerPerUke((arbeidsavtale.getStillingsprosent() / 100.0) * 40.0);
                }
            }

            if (arbeidsavtale.getYrke().length() != 7) {
                arbeidsavtale.setYrke(yrkeskoder.getKoder().get(rand.nextInt(yrkeskoder.getKoder().size())));
            }
        }
    }

}
