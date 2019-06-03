package no.nav.registre.aareg.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import no.nav.registre.aareg.AaregSaveInHodejegerenRequest;
import no.nav.registre.aareg.IdentMedData;
import no.nav.registre.aareg.consumer.rs.AaregSyntetisererenConsumer;
import no.nav.registre.aareg.consumer.rs.AaregstubConsumer;
import no.nav.registre.aareg.consumer.rs.HodejegerenConsumer;
import no.nav.registre.aareg.consumer.rs.responses.ArbeidsforholdsResponse;
import no.nav.registre.aareg.consumer.rs.responses.StatusFraAaregstubResponse;
import no.nav.registre.aareg.domain.Arbeidsforhold;
import no.nav.registre.aareg.provider.rs.requests.SyntetiserAaregRequest;

@Service
@Slf4j
public class SyntetiseringService {

    private static final String AAREG_NAME = "aareg";

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private AaregSyntetisererenConsumer aaregSyntetisererenConsumer;

    @Autowired
    private AaregstubConsumer aaregstubConsumer;

    @Autowired
    private Random rand;

    public ResponseEntity opprettArbeidshistorikkOgSendTilAaregstub(SyntetiserAaregRequest syntetiserAaregRequest, Boolean lagreIAareg) {
        List<String> levendeIdenter = hodejegerenConsumer.finnLevendeIdenter(syntetiserAaregRequest.getAvspillergruppeId());
        List<String> nyeIdenter = new ArrayList<>(syntetiserAaregRequest.getAntallNyeIdenter());
        List<String> utvalgteIdenter = new ArrayList<>(aaregstubConsumer.hentEksisterendeIdenter());
        levendeIdenter.removeAll(utvalgteIdenter);

        int antallNyeIdenter = syntetiserAaregRequest.getAntallNyeIdenter();
        if (antallNyeIdenter > levendeIdenter.size()) {
            antallNyeIdenter = levendeIdenter.size();
            log.info("Fant ikke nok ledige identer i avspillergruppe. Lager arbeidsforhold på {} identer.", antallNyeIdenter);
        }

        for (int i = 0; i < antallNyeIdenter; i++) {
            nyeIdenter.add(levendeIdenter.remove(rand.nextInt(levendeIdenter.size())));
        }

        utvalgteIdenter.addAll(nyeIdenter);
        List<ArbeidsforholdsResponse> syntetiserteArbeidsforhold = aaregSyntetisererenConsumer.getSyntetiserteArbeidsforholdsmeldinger(utvalgteIdenter);
        for (ArbeidsforholdsResponse arbeidsforholdsResponse : syntetiserteArbeidsforhold) {
            arbeidsforholdsResponse.setEnvironments(Arrays.asList(syntetiserAaregRequest.getMiljoe()));
        }

        StatusFraAaregstubResponse statusFraAaregstubResponse = aaregstubConsumer.sendTilAaregstub(syntetiserteArbeidsforhold, lagreIAareg);

        StringBuilder statusFraAaregstub = new StringBuilder();

        if (!CollectionUtils.isEmpty(statusFraAaregstubResponse.getIdenterLagretIStub())) {
            statusFraAaregstub
                    .append("Identer som ble lagret i aaregstub: ")
                    .append(statusFraAaregstubResponse.getIdenterLagretIStub())
                    .append(". ");
        }

        if (!CollectionUtils.isEmpty(statusFraAaregstubResponse.getIdenterLagretIAareg())) {
            statusFraAaregstub
                    .append("Identer som ble sendt til aareg: ")
                    .append(statusFraAaregstubResponse.getIdenterLagretIAareg())
                    .append(". ");
        }

        lagreArbeidsforholdIHodejegeren(statusFraAaregstubResponse, syntetiserteArbeidsforhold);

        log.info(statusFraAaregstub.toString());

        if (!CollectionUtils.isEmpty(statusFraAaregstubResponse.getIdenterSomIkkeKunneLagresIAareg())) {
            JsonNode statusFeilmeldinger = new ObjectMapper().valueToTree(statusFraAaregstubResponse.getIdenterSomIkkeKunneLagresIAareg());
            log.error("Status på identer som ikke kunne sendes til aareg: {}", statusFeilmeldinger);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(String.format("Noe feilet under lagring til aaregstub. {%s}. {%s}",
                    statusFeilmeldinger.toString(),
                    statusFraAaregstub.toString()));
        }

        return ResponseEntity.ok().body(statusFraAaregstub.toString());
    }

    private void lagreArbeidsforholdIHodejegeren(StatusFraAaregstubResponse statusFraAaregstubResponse, List<ArbeidsforholdsResponse> syntetiserteArbeidsforhold) {
        List<IdentMedData> arbeidsforholdLagretIAareg = finnArbeidsforholdLagretIAareg(statusFraAaregstubResponse.getIdenterLagretIAareg(), syntetiserteArbeidsforhold);
        if (!arbeidsforholdLagretIAareg.isEmpty()) {
            AaregSaveInHodejegerenRequest hodejegerenRequest = new AaregSaveInHodejegerenRequest(AAREG_NAME, arbeidsforholdLagretIAareg);

            List<String> lagredeIdenter = hodejegerenConsumer.saveHistory(hodejegerenRequest);

            List<IdentMedData> identerMedData = hodejegerenRequest.getIdentMedData();
            if (lagredeIdenter.size() < identerMedData.size()) {
                List<String> identerSomIkkeBleLagret = new ArrayList<>(identerMedData.size());
                for (IdentMedData ident : identerMedData) {
                    identerSomIkkeBleLagret.add(ident.getId());
                }
                identerSomIkkeBleLagret.removeAll(lagredeIdenter);
                log.warn("Kunne ikke lagre historikk på alle identer. Identer som ikke ble lagret: {}", identerSomIkkeBleLagret);
            }
        }
    }

    private List<IdentMedData> finnArbeidsforholdLagretIAareg(List<String> fnrs, List<ArbeidsforholdsResponse> syntetiserteArbeidsforhold) {
        List<IdentMedData> arbeidsforholdFunnet = new ArrayList<>();
        if (fnrs != null) {
            for (String fnr : fnrs) {
                for (ArbeidsforholdsResponse arbeidsforholdsResponse : syntetiserteArbeidsforhold) {
                    if (arbeidsforholdsResponse.getArbeidsforhold().getArbeidstaker() != null
                            && arbeidsforholdsResponse.getArbeidsforhold().getArbeidstaker().getIdent() != null
                            && fnr.equals(arbeidsforholdsResponse.getArbeidsforhold().getArbeidstaker().getIdent())) {
                        arbeidsforholdFunnet.add(new IdentMedData(fnr, Collections.singletonList(arbeidsforholdsResponse.getArbeidsforhold())));
                        break;
                    }
                }
            }
        }
        return arbeidsforholdFunnet;
    }

    public StatusFraAaregstubResponse sendArbeidsforholdTilAareg(List<ArbeidsforholdsResponse> arbeidsforhold, boolean fyllUtArbeidsforhold) {
        if (fyllUtArbeidsforhold) {
            List<String> identer = arbeidsforhold.stream().map(x -> x.getArbeidsforhold().getArbeidstaker().getIdent()).collect(Collectors.toList());

            List<ArbeidsforholdsResponse> syntetiserteArbeidsforhold = aaregSyntetisererenConsumer.getSyntetiserteArbeidsforholdsmeldinger(identer);

            for (int i = 0; i < arbeidsforhold.size() && i < syntetiserteArbeidsforhold.size(); i++) {
                Arbeidsforhold syntetisertArbeidsforhold = syntetiserteArbeidsforhold.get(i).getArbeidsforhold();
                Arbeidsforhold originaltArbeidsforhold = arbeidsforhold.get(i).getArbeidsforhold();

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

            return aaregstubConsumer.sendTilAaregstub(arbeidsforhold, true);
        } else {
            return aaregstubConsumer.sendTilAaregstub(arbeidsforhold, true);
        }
    }
}
