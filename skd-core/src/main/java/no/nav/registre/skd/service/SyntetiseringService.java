package no.nav.registre.skd.service;

import static no.nav.registre.skd.consumer.requests.HentIdenterRequest.IdentType.DNR;
import static no.nav.registre.skd.consumer.requests.HentIdenterRequest.IdentType.FNR;
import static no.nav.registre.skd.service.Endringskoder.FOEDSELSMELDING;
import static no.nav.registre.skd.service.Endringskoder.FOEDSELSNUMMERKORREKSJON;
import static no.nav.registre.skd.service.Endringskoder.INNVANDRING;
import static no.nav.registre.skd.service.Endringskoder.TILDELING_DNUMMER;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
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

    @Autowired
    private TpService tpService;

    private List<String> feiledeEndringskoder;

    public ResponseEntity puttIdenterIMeldingerOgLagre(GenereringsOrdreRequest genereringsOrdreRequest) {
        final Map<String, Integer> antallMeldingerPerEndringskode = genereringsOrdreRequest.getAntallMeldingerPerEndringskode();
        final List<Endringskoder> sorterteEndringskoder = filtrerOgSorterBestilteEndringskoder(antallMeldingerPerEndringskode.keySet());
        SkdMeldingerTilTpsRespons skdMeldingerTilTpsResponsTotal = new SkdMeldingerTilTpsRespons();

        String miljoe = genereringsOrdreRequest.getMiljoe();

        Map<String, List<String>> listerMedIdenter = opprettListerMedIdenter(genereringsOrdreRequest);

        HttpStatus httpStatus = HttpStatus.CREATED;
        List<Long> idsLagretITpsfMenIkkeTps = new ArrayList<>();
        feiledeEndringskoder = new ArrayList<>();

        List<String> nyeIdenterDenneEndringskoden;

        for (Endringskoder endringskode : sorterteEndringskoder) {
            List<Long> ids = new ArrayList<>();
            try {
                List<RsMeldingstype> syntetiserteSkdmeldinger = tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger(endringskode.getEndringskode(),
                        antallMeldingerPerEndringskode.get(endringskode.getEndringskode()));
                validationService.logAndRemoveInvalidMessages(syntetiserteSkdmeldinger, endringskode);

                nyeIdenterDenneEndringskoden = new ArrayList<>();

                if (Arrays.asList(INNVANDRING, FOEDSELSNUMMERKORREKSJON).contains(endringskode)) {
                    nyeIdenterDenneEndringskoden.addAll(nyeIdenterService.settInnNyeIdenterITrans1Meldinger(miljoe, FNR, syntetiserteSkdmeldinger));
                } else if (TILDELING_DNUMMER.equals(endringskode)) {
                    nyeIdenterDenneEndringskoden.addAll(nyeIdenterService.settInnNyeIdenterITrans1Meldinger(miljoe, DNR, syntetiserteSkdmeldinger));
                } else if (FOEDSELSMELDING.equals(endringskode)) {
                    nyeIdenterDenneEndringskoden.addAll(foedselService.behandleFoedselsmeldinger(FNR, syntetiserteSkdmeldinger, listerMedIdenter.get(LEVENDE_IDENTER_I_NORGE)));
                } else {
                    eksisterendeIdenterService.behandleEksisterendeIdenter(syntetiserteSkdmeldinger, listerMedIdenter, endringskode, miljoe);
                }

                ids = lagreSkdEndringsmeldingerITpsf(endringskode, syntetiserteSkdmeldinger, genereringsOrdreRequest);
                idsLagretITpsfMenIkkeTps.addAll(ids);

                SkdMeldingerTilTpsRespons skdMeldingerTilTpsRespons = sendSkdEndringsmeldingerTilTps(ids, genereringsOrdreRequest);
                skdMeldingerTilTpsResponsTotal.setAntallSendte(skdMeldingerTilTpsResponsTotal.getAntallSendte() + skdMeldingerTilTpsRespons.getAntallSendte());
                skdMeldingerTilTpsResponsTotal.setAntallFeilet(skdMeldingerTilTpsResponsTotal.getAntallFeilet() + skdMeldingerTilTpsRespons.getAntallFeilet());
                for (StatusPaaAvspiltSkdMelding status : skdMeldingerTilTpsRespons.getStatusFraFeilendeMeldinger()) {
                    skdMeldingerTilTpsResponsTotal.addStatusFraFeilendeMeldinger(status);
                }
                if (skdMeldingerTilTpsRespons.getTpsfIds() != null) {
                    for (Long tpsfId : skdMeldingerTilTpsRespons.getTpsfIds()) {
                        skdMeldingerTilTpsResponsTotal.addTpsfId(tpsfId);
                    }
                }

                fjernBrukteIdenterFraListerMedIdenter(listerMedIdenter);
                idsLagretITpsfMenIkkeTps.removeAll(ids);

                if (!nyeIdenterDenneEndringskoden.isEmpty()) {
                    List<String> feiledeTpIdenter = tpService.leggTilIdenterITp(nyeIdenterDenneEndringskoden, miljoe);
                    if (!feiledeTpIdenter.isEmpty()) {
                        log.error("Følgende identer kunne ikke lagres i TP: {}", feiledeTpIdenter.toString());
                    }
                }
            } catch (ManglendeInfoITpsException e) {
                httpStatus = loggExceptionOgLeggTilFeiletEndringskode(e,
                        "ManglendeInfoITPSException på endringskode " + endringskode.getEndringskode() + " i avspillergruppe " + genereringsOrdreRequest.getAvspillergruppeId() +
                                ". Skdmeldinger som er lagret i TPSF, men som ikke ble sendt til TPS har følgende id-er i TPSF: " + lagGrupperAvIder(idsLagretITpsfMenIkkeTps)
                                + " - Skdmeldinger som ble lagret i TPSF: " + lagGrupperAvIder(ids),
                        endringskode.getEndringskode());
            } catch (KunneIkkeSendeTilTpsException e) {
                httpStatus = loggExceptionOgLeggTilFeiletEndringskode(e,
                        "KunneIkkeSendeTilTpsException på endringskode " + endringskode.getEndringskode() + " i avspillergruppe " + genereringsOrdreRequest.getAvspillergruppeId() +
                                ". Skdmeldinger som er lagret i TPSF, men som ikke ble sendt til TPS har følgende id-er i TPSF: " + lagGrupperAvIder(idsLagretITpsfMenIkkeTps)
                                + " - Skdmeldinger som ble lagret i TPSF: " + lagGrupperAvIder(ids),
                        endringskode.getEndringskode());
            } catch (HttpStatusCodeException e) {
                log.error(hentMeldingFraJson(e.getResponseBodyAsString()), e); // Loggfører message i response body fordi e.getMessage() kun gir statuskodens tekst.
                log.warn(
                        "HttpStatusCodeException på endringskode {} i avspillergruppe {}. Skdmeldinger som er lagret i TPSF, men som ikke ble sendt til TPS har følgende id-er i TPSF: {} - Skdmeldinger som ble lagret i TPSF: {}",
                        endringskode.getEndringskode(), genereringsOrdreRequest.getAvspillergruppeId(), lagGrupperAvIder(idsLagretITpsfMenIkkeTps), lagGrupperAvIder(ids));
                feiledeEndringskoder.add(endringskode.getEndringskode());
                httpStatus = HttpStatus.CONFLICT;
            } catch (RuntimeException e) {
                httpStatus = loggExceptionOgLeggTilFeiletEndringskode(e,
                        "RuntimeException på endringskode " + endringskode.getEndringskode() + " i avspillergruppe " + genereringsOrdreRequest.getAvspillergruppeId() +
                                ". Skdmeldinger som er lagret i TPSF, men som ikke ble sendt til TPS har følgende id-er i TPSF: " + lagGrupperAvIder(idsLagretITpsfMenIkkeTps)
                                + " - Skdmeldinger som ble lagret i TPSF: " + lagGrupperAvIder(ids),
                        endringskode.getEndringskode());
            }
        }

        if (!feiledeEndringskoder.isEmpty()) {
            log.warn("Endringskoder som feilet i denne kjøringen: {}", feiledeEndringskoder);
        }

        return ResponseEntity.status(httpStatus).body(skdMeldingerTilTpsResponsTotal);
    }

    private void fjernBrukteIdenterFraListerMedIdenter(Map<String, List<String>> listerMedIdenter) {
        listerMedIdenter.get(GIFTE_IDENTER_I_NORGE).removeAll(listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN));
        listerMedIdenter.get(SINGLE_IDENTER_I_NORGE).removeAll(listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN));
        listerMedIdenter.get(LEVENDE_IDENTER_I_NORGE).removeAll(listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN));
    }

    private HttpStatus loggExceptionOgLeggTilFeiletEndringskode(Exception e, String feilmeldingTekst, String endringskode) {
        feiledeEndringskoder.add(endringskode);
        log.error(e.getMessage(), e);
        log.warn(feilmeldingTekst);
        return HttpStatus.CONFLICT;
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
