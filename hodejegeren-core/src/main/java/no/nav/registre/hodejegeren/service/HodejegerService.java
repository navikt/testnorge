package no.nav.registre.hodejegeren.service;

import no.nav.registre.hodejegeren.consumer.TpsSyntetisererenConsumer;
import no.nav.registre.hodejegeren.consumer.TpsfConsumer;
import no.nav.registre.hodejegeren.provider.rs.requests.GenereringsOrdreRequest;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest.IdentType.DNR;
import static no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest.IdentType.FNR;
import static no.nav.registre.hodejegeren.service.Endringskoder.*;

/**
 * Hoved-service i Hodejegeren. Her blir Tps Synt. kalt. Den genererer syntetiske skdmeldinger og returnerer dem til hodejegeren. Hodejegeren
 * finner nye identer i ident-pool og eksisterende identer TPS i angitt miljø som oppfyller kriterier for de ulike skdmeldingene (årsakskodene).
 * Disse identene fylles inn i skdmeldingene.
 * Til slutt lagres skdmeldingene i TPSF databasen, i Skd-endringsmelding-tabellen.
 */
@Service
public class HodejegerService {

    @Autowired
    private TpsSyntetisererenConsumer tpsSyntetisererenConsumer;

    @Autowired
    private NyeIdenterService nyeIdenterService;

    @Autowired
    private EksisterendeIdenterService eksisterendeIdenterService;

    @Autowired
    private FoedselService foedselService;

    @Autowired
    private TpsfConsumer tpsfConsumer;

    @Autowired
    private ValidationService validationService;

    public static final String TRANSAKSJONSTYPE = "1";

    public static final String LEVENDE_IDENTER_I_NORGE = "levendeIdenterINorge";
    public static final String GIFTE_IDENTER_I_NORGE = "gifteIdenterINorge";
    public static final String SINGLE_IDENTER_I_NORGE = "singleIdenterINorge";
    public static final String BRUKTE_IDENTER_I_DENNE_BOLKEN = "brukteIdenterIDenneBolken";

    public List<Long> puttIdenterIMeldingerOgLagre(GenereringsOrdreRequest genereringsOrdreRequest) {
        final Map<String, Integer> antallMeldingerPerEndringskode = genereringsOrdreRequest.getAntallMeldingerPerEndringskode();
        final List<Endringskoder> sorterteEndringskoder = filtrerOgSorterBestilteEndringskoder(antallMeldingerPerEndringskode.keySet());
        List<Long> ids = new ArrayList<>();

        String environment = genereringsOrdreRequest.getMiljoe();

        Map<String, List<String>> listerMedIdenter = createListerMedIdenter(genereringsOrdreRequest);

        for (Endringskoder endringskode : sorterteEndringskoder) {
            List<RsMeldingstype> syntetiserteSkdmeldinger = tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger(endringskode.getEndringskode(), antallMeldingerPerEndringskode.get(endringskode.getEndringskode()));
            validationService.logAndRemoveInvalidMessages(syntetiserteSkdmeldinger);

            if (Arrays.asList(INNVANDRING, FOEDSELSNUMMERKORREKSJON).contains(endringskode)) {
                nyeIdenterService.settInnNyeIdenterITrans1Meldinger(FNR, syntetiserteSkdmeldinger);
            } else if (TILDELING_DNUMMER.equals(endringskode)) {
                nyeIdenterService.settInnNyeIdenterITrans1Meldinger(DNR, syntetiserteSkdmeldinger);
            } else if (FOEDSELSMELDING.equals(endringskode)) {
                foedselService.behandleFoedselsmeldinger(FNR, syntetiserteSkdmeldinger, listerMedIdenter.get(LEVENDE_IDENTER_I_NORGE));
            } else {
                eksisterendeIdenterService.behandleEksisterendeIdenter(syntetiserteSkdmeldinger, listerMedIdenter, endringskode, environment);
            }

            ids.addAll(tpsfConsumer.saveSkdEndringsmeldingerInTPSF(genereringsOrdreRequest.getGruppeId(), syntetiserteSkdmeldinger));

            listerMedIdenter.get(GIFTE_IDENTER_I_NORGE).removeAll(listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN));
            listerMedIdenter.get(SINGLE_IDENTER_I_NORGE).removeAll(listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN));
            listerMedIdenter.get(LEVENDE_IDENTER_I_NORGE).removeAll(listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN));
        }
        return ids;
    }

    private List<Endringskoder> filtrerOgSorterBestilteEndringskoder(Set<String> endringskode) {
        List<Endringskoder> sorterteEndringskoder = Arrays.asList(Endringskoder.values());
        return sorterteEndringskoder.stream().filter(kode -> endringskode.contains(kode.getEndringskode())).collect(Collectors.toList());
    }

    private Map<String, List<String>> createListerMedIdenter(GenereringsOrdreRequest genereringsOrdreRequest) {
        Map<String, List<String>> listerMedIdenter = new HashMap<>();

        List<String> opprettedeIdenterITpsf = new ArrayList<>();
        opprettedeIdenterITpsf.addAll(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(
                genereringsOrdreRequest.getGruppeId(), Arrays.asList(
                        FOEDSELSMELDING.getAarsakskode(),
                        INNVANDRING.getAarsakskode(),
                        FOEDSELSNUMMERKORREKSJON.getAarsakskode(),
                        TILDELING_DNUMMER.getAarsakskode()),
                TRANSAKSJONSTYPE));

        List<String> doedeOgUtvandredeIdenter = new ArrayList<>();
        doedeOgUtvandredeIdenter.addAll(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(
                genereringsOrdreRequest.getGruppeId(), Arrays.asList(
                        Endringskoder.DOEDSMELDING.getAarsakskode(),
                        Endringskoder.UTVANDRING.getAarsakskode()),
                TRANSAKSJONSTYPE));

        List<String> levendeIdenterINorge = new ArrayList<>();
        levendeIdenterINorge.addAll(opprettedeIdenterITpsf);
        levendeIdenterINorge.removeAll(doedeOgUtvandredeIdenter);
        listerMedIdenter.put(LEVENDE_IDENTER_I_NORGE, levendeIdenterINorge);

        List<String> gifteIdenterINorge = new ArrayList<>();
        gifteIdenterINorge.addAll(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(
                genereringsOrdreRequest.getGruppeId(), Arrays.asList(
                        Endringskoder.VIGSEL.getAarsakskode()),
                TRANSAKSJONSTYPE));
        gifteIdenterINorge.removeAll(doedeOgUtvandredeIdenter);
        listerMedIdenter.put(GIFTE_IDENTER_I_NORGE, gifteIdenterINorge);

        List<String> singleIdenterINorge = new ArrayList<>();
        singleIdenterINorge.addAll(levendeIdenterINorge);
        singleIdenterINorge.removeAll(gifteIdenterINorge);
        listerMedIdenter.put(SINGLE_IDENTER_I_NORGE, singleIdenterINorge);

        List<String> brukteIdenterIDenneBolken = new ArrayList<>();
        listerMedIdenter.put(BRUKTE_IDENTER_I_DENNE_BOLKEN, brukteIdenterIDenneBolken);

        return listerMedIdenter;
    }
}
