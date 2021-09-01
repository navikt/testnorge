package no.nav.registre.skd.service;

import static no.nav.registre.skd.consumer.requests.HentIdenterRequest.IdentType.DNR;
import static no.nav.registre.skd.consumer.requests.HentIdenterRequest.IdentType.FNR;
import static no.nav.registre.skd.service.Endringskoder.FOEDSELSMELDING;
import static no.nav.registre.skd.service.Endringskoder.FOEDSELSNUMMERKORREKSJON;
import static no.nav.registre.skd.service.Endringskoder.INNVANDRING;
import static no.nav.registre.skd.service.Endringskoder.TILDELING_DNUMMER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.skd.consumer.TpsSyntetisererenConsumer;
import no.nav.registre.skd.consumer.TpsfConsumer;
import no.nav.registre.skd.consumer.requests.SendToTpsRequest;
import no.nav.registre.skd.consumer.response.SkdMeldingerTilTpsRespons;
import no.nav.registre.skd.exceptions.KunneIkkeSendeTilTpsException;
import no.nav.registre.skd.exceptions.ManglendeInfoITpsException;
import no.nav.registre.skd.provider.rs.requests.GenereringsOrdreRequest;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import no.nav.registre.skd.skdmelding.RsMeldingstype1Felter;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyntetiseringService {

    static final String FEILMELDING = "Feil på endringskode %s i avspillergruppe %d. Skdmeldinger som er lagret i TPSF, men som ikke ble sendt til TPS har følgende id-er i TPSF: %s - Skdmeldinger som ble lagret i TPSF: %s";
    static final String LEVENDE_IDENTER_I_NORGE = "levendeIdenterINorge";
    static final String GIFTE_IDENTER_I_NORGE = "gifteIdenterINorge";
    static final String FOEDTE_IDENTER = "foedteIdenter";
    static final String SINGLE_IDENTER_I_NORGE = "singleIdenterINorge";
    static final String BRUKTE_IDENTER_I_DENNE_BOLKEN = "brukteIdenterIDenneBolken";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final HodejegerenConsumer hodejegerenConsumer;

    private final TpsfConsumer tpsfConsumer;

    private final TpsSyntetisererenConsumer tpsSyntetisererenConsumer;

    private final ValidationService validationService;

    private final NyeIdenterService nyeIdenterService;

    private final FoedselService foedselService;

    private final EksisterendeIdenterService eksisterendeIdenterService;

    private final TpService tpService;

    private final PersonService personService;

    private List<String> feiledeEndringskoder;

    public ResponseEntity<SkdMeldingerTilTpsRespons> puttIdenterIMeldingerOgLagre(
            GenereringsOrdreRequest genereringsOrdreRequest
    ) {
        final var antallMeldingerPerEndringskode = genereringsOrdreRequest.getAntallMeldingerPerEndringskode();
        final var sorterteEndringskoder = filtrerOgSorterBestilteEndringskoder(antallMeldingerPerEndringskode.keySet());
        var skdMeldingerTilTpsResponsTotal = new SkdMeldingerTilTpsRespons();

        var miljoe = genereringsOrdreRequest.getMiljoe();

        var listerMedIdenter = opprettListerMedIdenter(genereringsOrdreRequest);

        var httpStatus = HttpStatus.CREATED;
        List<Long> idsLagretITpsfMenIkkeTps = new ArrayList<>();
        feiledeEndringskoder = new ArrayList<>();

        List<String> nyeIdenterDenneEndringskoden;

        for (var endringskode : sorterteEndringskoder) {
            List<Long> ids = new ArrayList<>();
            try {
                List<RsMeldingstype> syntetiserteSkdmeldinger = tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger(endringskode.getEndringskode(),
                        antallMeldingerPerEndringskode.get(endringskode.getEndringskode()));
                validationService.logAndRemoveInvalidMessages(syntetiserteSkdmeldinger, endringskode);

                nyeIdenterDenneEndringskoden = new ArrayList<>();

                if (Arrays.asList(INNVANDRING, FOEDSELSNUMMERKORREKSJON).contains(endringskode)) {
                    nyeIdenterDenneEndringskoden.addAll(nyeIdenterService.settInnNyeIdenterITrans1Meldinger(FNR, syntetiserteSkdmeldinger));
                } else if (TILDELING_DNUMMER.equals(endringskode)) {
                    nyeIdenterDenneEndringskoden.addAll(nyeIdenterService.settInnNyeIdenterITrans1Meldinger(DNR, syntetiserteSkdmeldinger));
                } else if (FOEDSELSMELDING.equals(endringskode)) {
                    nyeIdenterDenneEndringskoden.addAll(foedselService.behandleFoedselsmeldinger(FNR, syntetiserteSkdmeldinger, listerMedIdenter.get(LEVENDE_IDENTER_I_NORGE)));
                } else {
                    eksisterendeIdenterService.behandleEksisterendeIdenter(syntetiserteSkdmeldinger, listerMedIdenter, endringskode, miljoe);
                }
                ids = lagreSkdEndringsmeldingerITpsf(endringskode, syntetiserteSkdmeldinger, genereringsOrdreRequest);
                idsLagretITpsfMenIkkeTps.addAll(ids);

                sendSkdMeldingerTilTpsOgOppdaterStatus(skdMeldingerTilTpsResponsTotal, ids, genereringsOrdreRequest);

                fjernBrukteIdenterFraListerMedIdenter(listerMedIdenter);
                idsLagretITpsfMenIkkeTps.removeAll(ids);

                leggNyeIdenterIPdl(miljoe, nyeIdenterDenneEndringskoden, endringskode, genereringsOrdreRequest);

            } catch (ManglendeInfoITpsException e) {
                httpStatus = loggExceptionOgLeggTilFeiletEndringskode(e,
                        "ManglendeInfoITPSException: " + String.format(FEILMELDING, endringskode.getEndringskode(),
                                genereringsOrdreRequest.getAvspillergruppeId(), lagGrupperAvIder(idsLagretITpsfMenIkkeTps),
                                lagGrupperAvIder(ids)), endringskode.getEndringskode());
            } catch (KunneIkkeSendeTilTpsException e) {
                httpStatus = loggExceptionOgLeggTilFeiletEndringskode(e,
                        "KunneIkkeSendeTilTpsException: " + String.format(FEILMELDING, endringskode.getEndringskode(),
                                genereringsOrdreRequest.getAvspillergruppeId(), lagGrupperAvIder(idsLagretITpsfMenIkkeTps),
                                lagGrupperAvIder(ids)), endringskode.getEndringskode());
            } catch (HttpStatusCodeException e) {
                log.error(hentMeldingFraJson(e.getResponseBodyAsString()), e); // Loggfører message i response body fordi e.getMessage() kun gir statuskodens tekst.
                log.warn(
                        "HttpStatusCodeException på endringskode {} i avspillergruppe {}. Skdmeldinger som er lagret i TPSF, men som ikke ble sendt til TPS har følgende id-er i TPSF: {} - Skdmeldinger som ble lagret i TPSF: {}",
                        endringskode.getEndringskode(), genereringsOrdreRequest.getAvspillergruppeId(), lagGrupperAvIder(idsLagretITpsfMenIkkeTps), lagGrupperAvIder(ids));
                feiledeEndringskoder.add(endringskode.getEndringskode());
                httpStatus = HttpStatus.CONFLICT;
            } catch (RuntimeException e) {
                httpStatus = loggExceptionOgLeggTilFeiletEndringskode(e,
                        "RuntimeException: " + String.format(FEILMELDING, endringskode.getEndringskode(),
                                genereringsOrdreRequest.getAvspillergruppeId(), lagGrupperAvIder(idsLagretITpsfMenIkkeTps),
                                lagGrupperAvIder(ids)), endringskode.getEndringskode());
            }
        }

        if (!feiledeEndringskoder.isEmpty()) {
            log.warn("Endringskoder som feilet i denne kjøringen: {}", feiledeEndringskoder);
        }

        return ResponseEntity.status(httpStatus).body(skdMeldingerTilTpsResponsTotal);
    }

    private void leggNyeIdenterIPdl(String miljoe, List<String> nyeIdenterDenneEndringskoden, Endringskoder endringskode, GenereringsOrdreRequest request) {
        if (!nyeIdenterDenneEndringskoden.isEmpty()) {
            var feiledeTpIdenter = tpService.leggTilIdenterITp(nyeIdenterDenneEndringskoden, miljoe);
            if (!feiledeTpIdenter.isEmpty()) {
                log.error("Følgende identer kunne ikke lagres i TP: {}", feiledeTpIdenter.toString());
            }
            if (Arrays.asList(FOEDSELSMELDING, INNVANDRING).contains(endringskode)) {
                personService.leggTilIdenterIPdl(nyeIdenterDenneEndringskoden, request.getAvspillergruppeId(), request.getMiljoe());
            }
        }
    }

    private void fjernBrukteIdenterFraListerMedIdenter(
            Map<String, List<String>> listerMedIdenter
    ) {
        listerMedIdenter.get(GIFTE_IDENTER_I_NORGE).removeAll(listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN));
        listerMedIdenter.get(SINGLE_IDENTER_I_NORGE).removeAll(listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN));
        listerMedIdenter.get(LEVENDE_IDENTER_I_NORGE).removeAll(listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN));
    }

    private HttpStatus loggExceptionOgLeggTilFeiletEndringskode(
            Exception e,
            String feilmeldingTekst,
            String endringskode
    ) {
        feiledeEndringskoder.add(endringskode);
        log.error(e.getMessage(), e);
        log.warn(feilmeldingTekst);
        return HttpStatus.CONFLICT;
    }

    private String hentMeldingFraJson(
            String responseBody
    ) {
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
    private List<Endringskoder> filtrerOgSorterBestilteEndringskoder(
            Set<String> endringskode
    ) {
        var sorterteEndringskoder = Arrays.asList(Endringskoder.values());
        return sorterteEndringskoder.stream().filter(kode -> endringskode.contains(kode.getEndringskode())).collect(Collectors.toList());
    }

    private List<Long> lagreSkdEndringsmeldingerITpsf(
            Endringskoder endringskode,
            List<RsMeldingstype> syntetiserteSkdmeldinger,
            GenereringsOrdreRequest genereringsOrdreRequest
    ) {
        try {
            return new ArrayList<>(tpsfConsumer.saveSkdEndringsmeldingerInTPSF(genereringsOrdreRequest.getAvspillergruppeId(), syntetiserteSkdmeldinger));
        } catch (Exception e) {
            var message = new StringBuilder(120).append("Noe feilet under lagring til TPSF: ")
                    .append(e.getMessage())
                    .append(" - Endringskode: ")
                    .append(endringskode.getEndringskode());
            if (Arrays.asList(INNVANDRING, FOEDSELSNUMMERKORREKSJON, TILDELING_DNUMMER, FOEDSELSMELDING).contains(endringskode)) {
                message.append(" - Rekvirerte fødselsnumre i denne batchen: ");
                for (var rs : syntetiserteSkdmeldinger) {
                    message.append(((RsMeldingstype1Felter) rs).getFodselsdato())
                            .append(((RsMeldingstype1Felter) rs).getPersonnummer())
                            .append(", ");
                }
            }
            log.warn(message.toString());
            throw e;
        }
    }

    private SkdMeldingerTilTpsRespons sendSkdEndringsmeldingerTilTps(
            List<Long> ids,
            GenereringsOrdreRequest genereringsOrdreRequest
    ) {
        var sendToTpsRequest = new SendToTpsRequest(genereringsOrdreRequest.getMiljoe(), ids);
        try {
            return tpsfConsumer.sendSkdmeldingerToTps(genereringsOrdreRequest.getAvspillergruppeId(), sendToTpsRequest);
        } catch (HttpStatusCodeException e) {
            throw new KunneIkkeSendeTilTpsException(e.getMessage(), e);
        }
    }

    private Map<String, List<String>> opprettListerMedIdenter(
            GenereringsOrdreRequest genereringsOrdreRequest
    ) {
        Map<String, List<String>> listerMedIdenter = new HashMap<>();

        var levendeIdenterINorge = finnLevendeIdenter(genereringsOrdreRequest.getAvspillergruppeId());
        listerMedIdenter.put(LEVENDE_IDENTER_I_NORGE, levendeIdenterINorge);
        var message = new StringBuilder("Antall identer i lister fra TPSF: - ")
                .append(LEVENDE_IDENTER_I_NORGE)
                .append(": ")
                .append(levendeIdenterINorge.size());

        var doedeIdenterINorge = finnDoedeOgUtvandredeIdenter(genereringsOrdreRequest.getAvspillergruppeId());

        var gifteIdenterINorge = finnGifteIdenter(genereringsOrdreRequest.getAvspillergruppeId());
        gifteIdenterINorge.removeAll(doedeIdenterINorge);
        listerMedIdenter.put(GIFTE_IDENTER_I_NORGE, gifteIdenterINorge);
        message.append(" - ")
                .append(GIFTE_IDENTER_I_NORGE)
                .append(": ").append(gifteIdenterINorge.size());

        var singleIdenterINorge = new ArrayList<>(levendeIdenterINorge);
        singleIdenterINorge.removeAll(gifteIdenterINorge);
        listerMedIdenter.put(SINGLE_IDENTER_I_NORGE, singleIdenterINorge);
        message.append(" - ")
                .append(SINGLE_IDENTER_I_NORGE)
                .append(": ").append(singleIdenterINorge.size());

        var foedteIdenter = finnFoedteIdenter(genereringsOrdreRequest.getAvspillergruppeId());
        foedteIdenter.removeAll(doedeIdenterINorge);
        listerMedIdenter.put(FOEDTE_IDENTER, foedteIdenter);
        message.append(" - ")
                .append(FOEDTE_IDENTER)
                .append(": ").append(foedteIdenter.size());

        List<String> brukteIdenterIDenneBolken = new ArrayList<>();
        listerMedIdenter.put(BRUKTE_IDENTER_I_DENNE_BOLKEN, brukteIdenterIDenneBolken);

        log.info(message.toString());

        return listerMedIdenter;
    }

    /**
     * Metoden tar inn en liste av id-er og oppretter en ny liste der inkrementerende id-er er skrevet som range for å spare plass
     * (f.eks vil 1, 2, 3 skrives som 1 - 3)
     */
    private List<String> lagGrupperAvIder(
            List<Long> ids
    ) {
        List<String> idsWithRange = new ArrayList<>();
        var rangeStarted = false;
        var rangeToAdd = new StringBuilder();

        for (int i = 0; i < ids.size(); i++) {
            if (sisteElementIListe(ids, idsWithRange, rangeStarted, rangeToAdd, i))
                break;
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

    private boolean sisteElementIListe(List<Long> ids, List<String> idsWithRange, boolean rangeStarted, StringBuilder rangeToAdd, int i) {
        if (i >= ids.size() - 1) {
            if (rangeStarted) {
                rangeToAdd.append(ids.get(i));
                idsWithRange.add(rangeToAdd.toString());
            } else {
                idsWithRange.add(ids.get(i).toString());
            }
            return true;
        }
        return false;
    }

    private void sendSkdMeldingerTilTpsOgOppdaterStatus(
            SkdMeldingerTilTpsRespons skdMeldingerTilTpsResponsTotal,
            List<Long> ids,
            GenereringsOrdreRequest genereringsOrdreRequest
    ) {
        var skdMeldingerTilTpsRespons = sendSkdEndringsmeldingerTilTps(ids, genereringsOrdreRequest);
        skdMeldingerTilTpsResponsTotal.setAntallSendte(skdMeldingerTilTpsResponsTotal.getAntallSendte() + skdMeldingerTilTpsRespons.getAntallSendte());
        skdMeldingerTilTpsResponsTotal.setAntallFeilet(skdMeldingerTilTpsResponsTotal.getAntallFeilet() + skdMeldingerTilTpsRespons.getAntallFeilet());
        for (var status : skdMeldingerTilTpsRespons.getStatusFraFeilendeMeldinger()) {
            if (skdMeldingerTilTpsResponsTotal.getStatusFraFeilendeMeldinger() == null) {
                skdMeldingerTilTpsResponsTotal.setStatusFraFeilendeMeldinger(new ArrayList<>());
            }
            skdMeldingerTilTpsResponsTotal.getStatusFraFeilendeMeldinger().add(status);
        }
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "hodejegeren" })
    private List<String> finnLevendeIdenter(
            Long avspillergruppeId
    ) {
        return hodejegerenConsumer.getLevende(avspillergruppeId);
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "hodejegeren" })
    private List<String> finnDoedeOgUtvandredeIdenter(
            Long avspillergruppeId
    ) {
        return hodejegerenConsumer.getDoedeOgUtvandrede(avspillergruppeId);
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "hodejegeren" })
    private List<String> finnGifteIdenter(
            Long avspillergruppeId
    ) {
        return hodejegerenConsumer.getGifte(avspillergruppeId);
    }

    private List<String> finnFoedteIdenter(
            Long avspillergruppeId
    ) {
        return hodejegerenConsumer.getFoedte(avspillergruppeId, 0, 18);
    }
}
