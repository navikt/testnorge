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
import no.nav.registre.skd.exceptions.IkkeFullfoertBehandlingExceptionsContainer;
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
    private static final String FEILMELDING_TEKST = "Skdmeldinger som var ferdig behandlet før noe feilet, har følgende id-er i TPSF (avspillergruppe {}): {}";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

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
        List<Long> ids = new ArrayList<>();

        String miljoe = genereringsOrdreRequest.getMiljoe();

        Map<String, List<String>> listerMedIdenter = opprettListerMedIdenter(genereringsOrdreRequest);
        IkkeFullfoertBehandlingExceptionsContainer ikkeFullfoertBehandlingExceptionsContainer = null;

        for (Endringskoder endringskode : sorterteEndringskoder) {
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

                lagreSkdEndringsmeldingerITpsfOgOppdaterIds(ids, endringskode, syntetiserteSkdmeldinger, genereringsOrdreRequest);

                listerMedIdenter.get(GIFTE_IDENTER_I_NORGE).removeAll(listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN));
                listerMedIdenter.get(SINGLE_IDENTER_I_NORGE).removeAll(listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN));
                listerMedIdenter.get(LEVENDE_IDENTER_I_NORGE).removeAll(listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN));
            } catch (ManglendeInfoITpsException e) {
                e.getClass().getName();
                if (ikkeFullfoertBehandlingExceptionsContainer == null) {
                    ikkeFullfoertBehandlingExceptionsContainer = new IkkeFullfoertBehandlingExceptionsContainer();
                }
                ikkeFullfoertBehandlingExceptionsContainer.addIds(ids)
                        .addMessage(e.getMessage() + " (ManglendeInfoITPSException) - endringskode: " + endringskode.getEndringskode());

                log.error(e.getMessage(), e);
                String errorMessage = "ManglendeInfoITPSException: " + FEILMELDING_TEKST;
                log.warn(errorMessage, genereringsOrdreRequest.getAvspillergruppeId(), lagGrupperAvIder(ids));
            } catch (HttpStatusCodeException e) {
                if (ikkeFullfoertBehandlingExceptionsContainer == null) {
                    ikkeFullfoertBehandlingExceptionsContainer = new IkkeFullfoertBehandlingExceptionsContainer();
                }
                ikkeFullfoertBehandlingExceptionsContainer.addIds(ids)
                        .addMessage(e.getMessage() + " (HttpStatusCodeException) - endringskode: " + endringskode.getEndringskode())
                        .addCause(e);

                log.error(hentMeldingFraJson(e.getResponseBodyAsString()), e); // Loggfører message i response body fordi e.getMessage() kun gir statuskodens tekst.
                String errorMessage = "HttpStatusCodeException: " + FEILMELDING_TEKST;
                log.warn(errorMessage, genereringsOrdreRequest.getAvspillergruppeId(), lagGrupperAvIder(ids));
            } catch (RuntimeException e) {
                if (ikkeFullfoertBehandlingExceptionsContainer == null) {
                    ikkeFullfoertBehandlingExceptionsContainer = new IkkeFullfoertBehandlingExceptionsContainer();
                }
                ikkeFullfoertBehandlingExceptionsContainer.addIds(ids)
                        .addMessage(e.getMessage() + " (RuntimeException) - endringskode: " + endringskode.getEndringskode())
                        .addCause(e);

                log.error(e.getMessage(), e);
                String errorMessage = "RuntimeException: " + FEILMELDING_TEKST;
                log.warn(errorMessage, genereringsOrdreRequest.getAvspillergruppeId(), lagGrupperAvIder(ids));
            }
        }

        if (ikkeFullfoertBehandlingExceptionsContainer != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ikkeFullfoertBehandlingExceptionsContainer);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(ids);
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

    private void lagreSkdEndringsmeldingerITpsfOgOppdaterIds(List<Long> ids, Endringskoder endringskode,
            List<RsMeldingstype> syntetiserteSkdmeldinger, GenereringsOrdreRequest genereringsOrdreRequest) {
        try {
            ids.addAll(hodejegerenConsumer.lagreSkdEndringsmeldingerITpsf(genereringsOrdreRequest.getAvspillergruppeId(), syntetiserteSkdmeldinger));
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
