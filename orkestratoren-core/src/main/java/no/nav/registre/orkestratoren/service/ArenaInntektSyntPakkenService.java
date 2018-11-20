package no.nav.registre.orkestratoren.service;

import static no.nav.registre.orkestratoren.service.AarsakskoderTrans1.FOEDSELSMELDING;
import static no.nav.registre.orkestratoren.service.AarsakskoderTrans1.FOEDSELSNUMMERKORREKSJON;
import static no.nav.registre.orkestratoren.service.AarsakskoderTrans1.INNVANDRING;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.nav.registre.orkestratoren.consumer.rs.ArenaInntektSyntConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TpsfConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;

@Service
public class ArenaInntektSyntPakkenService {

    private static final String TRANSAKSJONSTYPE_1 = "1";
    @Autowired
    private TpsfConsumer tpsfConsumer;
    @Autowired
    private ArenaInntektSyntConsumer arenaInntektSyntConsumer;

    public List<String> genererEnInntektsmeldingPerFnrIInntektstub(SyntetiserInntektsmeldingRequest request) {
        List<String> levendeNordmennFnr = opprettListenLevendeNordmenn(request.getSkdMeldingGruppeId());
        List<String> inntektsmldMottakere = levendeNordmennFnr.subList(0, levendeNordmennFnr.size() / 2);
        arenaInntektSyntConsumer.genererEnInntektsmeldingPerFnrIInntektstub(inntektsmldMottakere);
        return inntektsmldMottakere;
    }

    private List<String> opprettListenLevendeNordmenn(Long gruppeId) {
        List<String> opprettedeIdenterITpsf = new ArrayList<>();
        opprettedeIdenterITpsf.addAll(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(gruppeId,
                Arrays.asList(
                        FOEDSELSMELDING.getAarsakskode(),
                        INNVANDRING.getAarsakskode(),
                        FOEDSELSNUMMERKORREKSJON.getAarsakskode()),
                TRANSAKSJONSTYPE_1));

        List<String> doedeOgUtvandredeIdenter = new ArrayList<>();
        doedeOgUtvandredeIdenter.addAll(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(gruppeId,
                Arrays.asList(
                        AarsakskoderTrans1.DOEDSMELDING.getAarsakskode(),
                        AarsakskoderTrans1.UTVANDRING.getAarsakskode()),
                TRANSAKSJONSTYPE_1));

        List<String> levendeIdenterINorge = new ArrayList<>();
        levendeIdenterINorge.addAll(opprettedeIdenterITpsf);
        levendeIdenterINorge.removeAll(doedeOgUtvandredeIdenter);

        return levendeIdenterINorge;
    }
}
