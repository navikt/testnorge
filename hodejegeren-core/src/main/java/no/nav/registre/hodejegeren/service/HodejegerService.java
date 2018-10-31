package no.nav.registre.hodejegeren.service;

import no.nav.registre.hodejegeren.consumer.TpsSyntetisererenConsumer;
import no.nav.registre.hodejegeren.consumer.TpsfConsumer;
import no.nav.registre.hodejegeren.provider.rs.requests.GenereringsOrdreRequest;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest.IdentType.DNR;
import static no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest.IdentType.FNR;

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
    private TpsfConsumer tpsfConsumer;

    @Autowired
    private ValidationService validationService;

    public static final String AKSJONSKODE = "A0";
    public static final String ENVIRONMENT = "T1";

    public static final String LEVENDE_IDENTER_I_NORGE = "levendeIdenterINorge";
    public static final String GIFTE_IDENTER_I_NORGE = "gifteIdenterINorge";
    public static final String SINGLE_IDENTER_I_NORGE = "singleIdenterINorge";
    public static final String BRUKTE_IDENTER_I_DENNE_BOLKEN = "brukteIdenterIDenneBolken";

    public List<Long> puttIdenterIMeldingerOgLagre(GenereringsOrdreRequest genereringsOrdreRequest) {
        final Map<String, Integer> antallMeldingerPerAarsakskode = genereringsOrdreRequest.getAntallMeldingerPerAarsakskode();
        final List<String> sorterteAarsakskoder = filtrerOgSorterBestilteAarsakskoder(antallMeldingerPerAarsakskode);
        List<Long> ids = new ArrayList<>();
        final String transaksjonstype = "1";

        Map<String, List<String>> listerMedIdenter = new HashMap<>();

        List<String> opprettedeIdenterITpsf = new ArrayList<>();
        opprettedeIdenterITpsf.addAll(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(
                genereringsOrdreRequest.getGruppeId(), Arrays.asList(
                        AarsakskoderTrans1.FOEDSELSMELDING.getAarsakskode(),
                        AarsakskoderTrans1.INNVANDRING.getAarsakskode(),
                        AarsakskoderTrans1.FOEDSELSNUMMERKORREKSJON.getAarsakskode(),
                        AarsakskoderTrans1.TILDELING_DNUMMER.getAarsakskode()),
                transaksjonstype));

        List<String> doedeOgUtvandredeIdenter = new ArrayList<>();
        doedeOgUtvandredeIdenter.addAll(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(
                genereringsOrdreRequest.getGruppeId(), Arrays.asList(
                        AarsakskoderTrans1.DOEDSMELDING.getAarsakskode(),
                        AarsakskoderTrans1.UTVANDRING.getAarsakskode()),
                transaksjonstype));

        List<String> levendeIdenterINorge = new ArrayList<>();
        levendeIdenterINorge.addAll(opprettedeIdenterITpsf);
        levendeIdenterINorge.removeAll(doedeOgUtvandredeIdenter);
        listerMedIdenter.put(LEVENDE_IDENTER_I_NORGE, levendeIdenterINorge);

        List<String> gifteIdenterINorge = new ArrayList<>();
        gifteIdenterINorge.addAll(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(
                genereringsOrdreRequest.getGruppeId(), Arrays.asList(
                        AarsakskoderTrans1.VIGSEL.getAarsakskode()),
                transaksjonstype));
        gifteIdenterINorge.removeAll(doedeOgUtvandredeIdenter);
        listerMedIdenter.put(GIFTE_IDENTER_I_NORGE, gifteIdenterINorge);

        List<String> singleIdenterINorge = new ArrayList<>();
        singleIdenterINorge.addAll(levendeIdenterINorge);
        singleIdenterINorge.removeAll(gifteIdenterINorge);
        listerMedIdenter.put(SINGLE_IDENTER_I_NORGE, singleIdenterINorge);

        List<String> brukteIdenterIDenneBolken = new ArrayList<>();
        listerMedIdenter.put(BRUKTE_IDENTER_I_DENNE_BOLKEN, brukteIdenterIDenneBolken);


        for (String aarsakskode : sorterteAarsakskoder) {
            List<RsMeldingstype> syntetiserteSkdmeldinger = tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger(aarsakskode, antallMeldingerPerAarsakskode.get(aarsakskode));
            validationService.logAndRemoveInvalidMessages(syntetiserteSkdmeldinger);

            if (Arrays.asList(AarsakskoderTrans1.FOEDSELSMELDING.getAarsakskode(),
                    AarsakskoderTrans1.INNVANDRING.getAarsakskode(),
                    AarsakskoderTrans1.FOEDSELSNUMMERKORREKSJON.getAarsakskode())
                    .contains(aarsakskode)) {
                nyeIdenterService.settInnNyeIdenterITrans1Meldinger(FNR, syntetiserteSkdmeldinger); //Bør jeg sette en øvre aldersgrense? åpent søk vil
            } else if (AarsakskoderTrans1.TILDELING_DNUMMER.getAarsakskode().equals(aarsakskode)) {
                nyeIdenterService.settInnNyeIdenterITrans1Meldinger(DNR, syntetiserteSkdmeldinger);
            } else {
                eksisterendeIdenterService.behandleEksisterendeIdenter(syntetiserteSkdmeldinger, listerMedIdenter, aarsakskode, antallMeldingerPerAarsakskode);
            }

            ids.addAll(tpsfConsumer.saveSkdEndringsmeldingerInTPSF(genereringsOrdreRequest.getGruppeId(), syntetiserteSkdmeldinger));

            gifteIdenterINorge.removeAll(brukteIdenterIDenneBolken);
            singleIdenterINorge.removeAll(brukteIdenterIDenneBolken);
            levendeIdenterINorge.removeAll(brukteIdenterIDenneBolken);
        }
        return ids;
    }

    private List<String> filtrerOgSorterBestilteAarsakskoder(Map<String, Integer> antallMeldingerPerAarsakskode) {
        List<String> sorterteAarsakskoder = Arrays.asList(AarsakskoderTrans1.values()).stream().map(AarsakskoderTrans1::getAarsakskode).collect(Collectors.toList());
        sorterteAarsakskoder.retainAll(antallMeldingerPerAarsakskode.keySet());
        return sorterteAarsakskoder;
    }
}
