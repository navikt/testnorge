package no.nav.registre.skd.service;

import static no.nav.registre.skd.consumer.requests.HentIdenterRequest.IdentType.DNR;
import static no.nav.registre.skd.consumer.requests.HentIdenterRequest.IdentType.FNR;
import static no.nav.registre.skd.service.Endringskoder.FOEDSELSMELDING;
import static no.nav.registre.skd.service.Endringskoder.FOEDSELSNUMMERKORREKSJON;
import static no.nav.registre.skd.service.Endringskoder.INNVANDRING;
import static no.nav.registre.skd.service.Endringskoder.TILDELING_DNUMMER;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.skd.consumer.HodejegerenConsumer;
import no.nav.registre.skd.consumer.TpsSyntetisererenConsumer;
import no.nav.registre.skd.consumer.TpsfConsumer;
import no.nav.registre.skd.consumer.requests.SendToTpsRequest;
import no.nav.registre.skd.consumer.response.SkdMeldingerTilTpsRespons;
import no.nav.registre.skd.consumer.response.StatusPaaAvspiltSkdMelding;
import no.nav.registre.skd.exceptions.KunneIkkeSendeTilTpsException;
import no.nav.registre.skd.exceptions.ManglendeInfoITpsException;
import no.nav.registre.skd.provider.rs.requests.GenereringsOrdreRequest;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import no.nav.registre.skd.skdmelding.RsMeldingstype1Felter;

@Service
@Slf4j
public class SyntetiseringService {

    public static final String LEVENDE_IDENTER_I_NORGE = "levendeIdenterINorge";
    public static final String GIFTE_IDENTER_I_NORGE = "gifteIdenterINorge";
    public static final String SINGLE_IDENTER_I_NORGE = "singleIdenterINorge";
    public static final String BRUKTE_IDENTER_I_DENNE_BOLKEN = "brukteIdenterIDenneBolken";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private TpsfConsumer tpsfConsumer;

    @Autowired
    private TpsSyntetisererenConsumer tpsSyntetisererenConsumer;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private NyeIdenterService nyeIdenterService;

    @Autowired
    private FoedselService foedselService;

    @Autowired
    private EksisterendeIdenterService eksisterendeIdenterService;

    public ResponseEntity puttIdenterIMeldingerOgLagre(GenereringsOrdreRequest genereringsOrdreRequest) {
        final Map<String, Integer> antallMeldingerPerEndringskode = genereringsOrdreRequest.getAntallMeldingerPerEndringskode();
        final List<Endringskoder> sorterteEndringskoder = filtrerOgSorterBestilteEndringskoder(antallMeldingerPerEndringskode.keySet());
        SkdMeldingerTilTpsRespons skdMeldingerTilTpsResponsTotal = new SkdMeldingerTilTpsRespons();

        String miljoe = genereringsOrdreRequest.getMiljoe();

        Map<String, List<String>> listerMedIdenter = opprettListerMedIdenter(genereringsOrdreRequest);

        HttpStatus httpStatus = HttpStatus.CREATED;

        for (Endringskoder endringskode : sorterteEndringskoder) {
            List<Long> ids = new ArrayList<>();
            try {
                List<RsMeldingstype> syntetiserteSkdmeldinger = tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger(endringskode.getEndringskode(),
                        antallMeldingerPerEndringskode.get(endringskode.getEndringskode()));
                validationService.logAndRemoveInvalidMessages(syntetiserteSkdmeldinger, endringskode);

                if (Arrays.asList(INNVANDRING, FOEDSELSNUMMERKORREKSJON).contains(endringskode)) {
                    nyeIdenterService.settInnNyeIdenterITrans1Meldinger(FNR, syntetiserteSkdmeldinger);
                } else if (TILDELING_DNUMMER.equals(endringskode)) {
                    nyeIdenterService.settInnNyeIdenterITrans1Meldinger(DNR, syntetiserteSkdmeldinger);
                } else if (FOEDSELSMELDING.equals(endringskode)) {
                    foedselService.behandleFoedselsmeldinger(FNR, syntetiserteSkdmeldinger, listerMedIdenter.get(LEVENDE_IDENTER_I_NORGE));
                } else {
                    eksisterendeIdenterService.behandleEksisterendeIdenter(syntetiserteSkdmeldinger, listerMedIdenter, endringskode, miljoe);
                }

                ids = lagreSkdEndringsmeldingerITpsf(endringskode, syntetiserteSkdmeldinger, genereringsOrdreRequest);

                SkdMeldingerTilTpsRespons skdMeldingerTilTpsRespons = sendSkdEndringsmeldingerTilTps(ids, genereringsOrdreRequest);
                skdMeldingerTilTpsResponsTotal.setAntallSendte(skdMeldingerTilTpsResponsTotal.getAntallSendte() + skdMeldingerTilTpsRespons.getAntallSendte());
                skdMeldingerTilTpsResponsTotal.setAntallFeilet(skdMeldingerTilTpsResponsTotal.getAntallFeilet() + skdMeldingerTilTpsRespons.getAntallFeilet());
                for (StatusPaaAvspiltSkdMelding status : skdMeldingerTilTpsRespons.getStatusFraFeilendeMeldinger()) {
                    skdMeldingerTilTpsResponsTotal.addStatusFraFeilendeMeldinger(status);
                }
                for (Long tpsfId : skdMeldingerTilTpsRespons.getTpsfIds()) {
                    skdMeldingerTilTpsResponsTotal.addTpsfId(tpsfId);
                }

                listerMedIdenter.get(GIFTE_IDENTER_I_NORGE).removeAll(listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN));
                listerMedIdenter.get(SINGLE_IDENTER_I_NORGE).removeAll(listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN));
                listerMedIdenter.get(LEVENDE_IDENTER_I_NORGE).removeAll(listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN));

            } catch (ManglendeInfoITpsException e) {
                log.error(e.getMessage(), e);
                log.warn("ManglendeInfoITPSException på endringskode {} i avspillergruppe {}.", endringskode.getEndringskode(), genereringsOrdreRequest.getAvspillergruppeId());
                httpStatus = HttpStatus.CONFLICT;
            } catch (KunneIkkeSendeTilTpsException e) {
                log.error(e.getMessage(), e);
                log.warn("KunneIkkeSendeTilTpsException på endringskode {} i avspillergruppe {}. Skdmeldinger som muligens ikke ble sendt til TPS har følgende id-er i TPSF: {}",
                        endringskode.getEndringskode(), genereringsOrdreRequest.getAvspillergruppeId(), lagGrupperAvIder(ids));
                httpStatus = HttpStatus.CONFLICT;
            } catch (HttpStatusCodeException e) {
                log.error(hentMeldingFraJson(e.getResponseBodyAsString()), e); // Loggfører message i response body fordi e.getMessage() kun gir statuskodens tekst.
                log.warn("HttpStatusCodeException på endringskode {} i avspillergruppe {}. Skdmeldinger som muligens ikke ble sendt til TPS har følgende id-er i TPSF: {}",
                        endringskode.getEndringskode(), genereringsOrdreRequest.getAvspillergruppeId(), lagGrupperAvIder(ids));
                httpStatus = HttpStatus.CONFLICT;
            } catch (RuntimeException e) {
                log.error(e.getMessage(), e);
                log.warn("RuntimeException på endringskode {} i avspillergruppe {}. Skdmeldinger som muligens ikke ble sendt til TPS har følgende id-er i TPSF: {}",
                        endringskode.getEndringskode(), genereringsOrdreRequest.getAvspillergruppeId(), lagGrupperAvIder(ids));
                httpStatus = HttpStatus.CONFLICT;
            }
        }

        return ResponseEntity.status(httpStatus).body(skdMeldingerTilTpsResponsTotal);
    }

    private String hentMeldingFraJson(String responseBody) {
        try {
            return objectMapper.readTree(responseBody).findValue("message").asText();
        } catch (IOException e) {
            return responseBody;
        }
    }

    /**
     * Metoden tar imot en liste med endringskoder, og sørger for at denne blir filtrert og sortert
     *
     * @param endringskode
     * @return sortert liste over endringskoder. Sortert på rekkefølge gitt i enumklasse {@link Endringskoder}
     */
    private List<Endringskoder> filtrerOgSorterBestilteEndringskoder(Set<String> endringskode) {
        List<Endringskoder> sorterteEndringskoder = Arrays.asList(Endringskoder.values());
        return sorterteEndringskoder.stream().filter(kode -> endringskode.contains(kode.getEndringskode())).collect(Collectors.toList());
    }

    private List<Long> lagreSkdEndringsmeldingerITpsf(Endringskoder endringskode, List<RsMeldingstype> syntetiserteSkdmeldinger, GenereringsOrdreRequest genereringsOrdreRequest) {
        try {
            return new ArrayList<>(tpsfConsumer.saveSkdEndringsmeldingerInTPSF(genereringsOrdreRequest.getAvspillergruppeId(), syntetiserteSkdmeldinger));
        } catch (Exception e) {
            StringBuilder message = new StringBuilder(120).append("Noe feilet under lagring til TPSF: ")
                    .append(e.getMessage())
                    .append(" - Endringskode: ")
                    .append(endringskode.getEndringskode());
            if (Arrays.asList(INNVANDRING, FOEDSELSNUMMERKORREKSJON, TILDELING_DNUMMER, FOEDSELSMELDING).contains(endringskode)) {
                message.append(" - Rekvirerte fødselsnumre i denne batchen: ");
                for (RsMeldingstype rs : syntetiserteSkdmeldinger) {
                    message.append(((RsMeldingstype1Felter) rs).getFodselsdato())
                            .append(((RsMeldingstype1Felter) rs).getPersonnummer())
                            .append(", ");
                }
            }
            log.warn(message.toString());
            throw e;
        }
    }

    private SkdMeldingerTilTpsRespons sendSkdEndringsmeldingerTilTps(List<Long> ids, GenereringsOrdreRequest genereringsOrdreRequest) {
        SendToTpsRequest sendToTpsRequest = new SendToTpsRequest(genereringsOrdreRequest.getMiljoe(), ids);
        try {
            return tpsfConsumer.sendSkdmeldingerToTps(genereringsOrdreRequest.getAvspillergruppeId(), sendToTpsRequest);
        } catch (HttpStatusCodeException e) {
            throw new KunneIkkeSendeTilTpsException(e.getMessage(), e);
        }
    }

    private Map<String, List<String>> opprettListerMedIdenter(GenereringsOrdreRequest genereringsOrdreRequest) {
        Map<String, List<String>> listerMedIdenter = new HashMap<>();

        List<String> levendeIdenterINorge = hodejegerenConsumer.finnLevendeIdenter(genereringsOrdreRequest.getAvspillergruppeId());
        listerMedIdenter.put(LEVENDE_IDENTER_I_NORGE, levendeIdenterINorge);
        StringBuilder message = new StringBuilder("Antall identer i lister fra TPSF: - ")
                .append(LEVENDE_IDENTER_I_NORGE)
                .append(": ")
                .append(levendeIdenterINorge.size());

        List<String> doedeIdenterINorge = hodejegerenConsumer.finnDoedeOgUtvandredeIdenter(genereringsOrdreRequest.getAvspillergruppeId());

        List<String> gifteIdenterINorge = hodejegerenConsumer.finnGifteIdenter(genereringsOrdreRequest.getAvspillergruppeId());
        gifteIdenterINorge.removeAll(doedeIdenterINorge);
        listerMedIdenter.put(GIFTE_IDENTER_I_NORGE, gifteIdenterINorge);
        message.append(" - ")
                .append(GIFTE_IDENTER_I_NORGE)
                .append(": ").append(gifteIdenterINorge.size());

        List<String> singleIdenterINorge = new ArrayList<>(levendeIdenterINorge);
        singleIdenterINorge.removeAll(gifteIdenterINorge);
        listerMedIdenter.put(SINGLE_IDENTER_I_NORGE, singleIdenterINorge);
        message.append(" - ")
                .append(SINGLE_IDENTER_I_NORGE)
                .append(": ").append(singleIdenterINorge.size());

        List<String> brukteIdenterIDenneBolken = new ArrayList<>();
        listerMedIdenter.put(BRUKTE_IDENTER_I_DENNE_BOLKEN, brukteIdenterIDenneBolken);

        log.info(message.toString());

        return listerMedIdenter;
    }

    /**
     * Metoden tar inn en liste av id-er og oppretter en ny liste der inkrementerende id-er er skrevet som range for å spare plass
     * (f.eks vil 1, 2, 3 skrives som 1 - 3)
     */
    private List<String> lagGrupperAvIder(List<Long> ids) {
        List<String> idsWithRange = new ArrayList<>();
        boolean rangeStarted = false;
        StringBuilder rangeToAdd = new StringBuilder();

        for (int i = 0; i < ids.size(); i++) {
            if (i >= ids.size() - 1) {
                if (rangeStarted) {
                    rangeToAdd.append(ids.get(i));
                    idsWithRange.add(rangeToAdd.toString());
                } else {
                    idsWithRange.add(ids.get(i).toString());
                }
                break;
            }
            if (ids.get(i + 1) == ids.get(i) + 1) {
                if (!rangeStarted) {
                    rangeToAdd = new StringBuilder(ids.get(i).toString()).append(" - ");
                    rangeStarted = true;
                }
            } else {
                if (rangeStarted) {
                    rangeToAdd.append(ids.get(i));
                    idsWithRange.add(rangeToAdd.toString());
                    rangeStarted = false;
                } else {
                    idsWithRange.add(ids.get(i).toString());
                }
            }
        }
        return idsWithRange;
    }
}
