package no.nav.registre.hodejegeren.service;

import static no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest.IdentType.DNR;
import static no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest.IdentType.FNR;
import static no.nav.registre.hodejegeren.service.Endringskoder.FOEDSELSMELDING;
import static no.nav.registre.hodejegeren.service.Endringskoder.FOEDSELSNUMMERKORREKSJON;
import static no.nav.registre.hodejegeren.service.Endringskoder.INNVANDRING;
import static no.nav.registre.hodejegeren.service.Endringskoder.TILDELING_DNUMMER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.hodejegeren.consumer.TpsSyntetisererenConsumer;
import no.nav.registre.hodejegeren.consumer.TpsfConsumer;
import no.nav.registre.hodejegeren.exception.IkkeFullfoertBehandlingExceptionsContainer;
import no.nav.registre.hodejegeren.exception.ManglendeInfoITpsException;
import no.nav.registre.hodejegeren.provider.rs.requests.GenereringsOrdreRequest;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype1Felter;

/**
 * Hoved-service i Hodejegeren. Her blir Tps Synt. kalt. Den genererer syntetiske skdmeldinger og returnerer dem til
 * hodejegeren. Hodejegeren finner nye identer i ident-pool og eksisterende identer TPS i angitt miljø som oppfyller kriterier
 * for de ulike skdmeldingene (årsakskodene). Disse identene fylles inn i skdmeldingene. Til slutt lagres skdmeldingene i TPSF
 * databasen, i Skd-endringsmelding-tabellen.
 */
@Service
@Slf4j
public class HodejegerService {

    public static final String TRANSAKSJONSTYPE = "1";
    public static final String LEVENDE_IDENTER_I_NORGE = "levendeIdenterINorge";
    public static final String GIFTE_IDENTER_I_NORGE = "gifteIdenterINorge";
    public static final String SINGLE_IDENTER_I_NORGE = "singleIdenterINorge";
    public static final String BRUKTE_IDENTER_I_DENNE_BOLKEN = "brukteIdenterIDenneBolken";
    private static final String FEILMELDING_TEKST = "Skdmeldinger som var ferdig behandlet før noe feilet, har følgende id-er i TPSF (avspillergruppe {}): {}";

    private static final ObjectMapper objectMapper = new ObjectMapper();

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

    public ResponseEntity puttIdenterIMeldingerOgLagre(GenereringsOrdreRequest genereringsOrdreRequest) {
        final Map<String, Integer> antallMeldingerPerEndringskode = genereringsOrdreRequest.getAntallMeldingerPerEndringskode();
        final List<Endringskoder> sorterteEndringskoder = filtrerOgSorterBestilteEndringskoder(antallMeldingerPerEndringskode.keySet());
        List<Long> ids = new ArrayList<>();

        String environment = genereringsOrdreRequest.getMiljoe();

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
                    eksisterendeIdenterService.behandleEksisterendeIdenter(syntetiserteSkdmeldinger, listerMedIdenter, endringskode, environment);
                }

                lagreSkdEndringsmeldingerITpsfOgOppdaterIds(ids, endringskode, syntetiserteSkdmeldinger, genereringsOrdreRequest);

                listerMedIdenter.get(GIFTE_IDENTER_I_NORGE).removeAll(listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN));
                listerMedIdenter.get(SINGLE_IDENTER_I_NORGE).removeAll(listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN));
                listerMedIdenter.get(LEVENDE_IDENTER_I_NORGE).removeAll(listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN));
            } catch (ManglendeInfoITpsException e) {
                ikkeFullfoertBehandlingExceptionsContainer = opprettOgFyllIkkeFullfoertBehandlingExceptionContainer(
                        ikkeFullfoertBehandlingExceptionsContainer, e, ids, endringskode);

                log.error(e.getMessage(), e);
                log.warn(FEILMELDING_TEKST, genereringsOrdreRequest.getGruppeId(), ids);
            } catch (HttpStatusCodeException e) {
                ikkeFullfoertBehandlingExceptionsContainer = opprettOgFyllIkkeFullfoertBehandlingExceptionContainer(
                        ikkeFullfoertBehandlingExceptionsContainer, e, ids, endringskode);

                log.error(getMessageFromJson(e.getResponseBodyAsString()), e); // Loggfører message i response body fordi e.getMessage() kun gir statuskodens tekst.
                log.warn(FEILMELDING_TEKST, genereringsOrdreRequest.getGruppeId(), ids);
            } catch (RuntimeException e) {
                ikkeFullfoertBehandlingExceptionsContainer = opprettOgFyllIkkeFullfoertBehandlingExceptionContainer(
                        ikkeFullfoertBehandlingExceptionsContainer, e, ids, endringskode);

                log.error(e.getMessage(), e);
                log.warn(FEILMELDING_TEKST, genereringsOrdreRequest.getGruppeId(), ids);
            }
        }

        if (ikkeFullfoertBehandlingExceptionsContainer != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ikkeFullfoertBehandlingExceptionsContainer);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(ids);
    }

    private String getMessageFromJson(String responseBody) {
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
            ids.addAll(tpsfConsumer.saveSkdEndringsmeldingerInTPSF(genereringsOrdreRequest.getGruppeId(), syntetiserteSkdmeldinger));
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
        StringBuilder message = new StringBuilder("Antall identer i lister fra TPSF: - ")
                .append(LEVENDE_IDENTER_I_NORGE)
                .append(": ")
                .append(levendeIdenterINorge.size());

        List<String> gifteIdenterINorge = new ArrayList<>();
        gifteIdenterINorge.addAll(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(
                genereringsOrdreRequest.getGruppeId(), Arrays.asList(
                        Endringskoder.VIGSEL.getAarsakskode()),
                TRANSAKSJONSTYPE));
        gifteIdenterINorge.removeAll(doedeOgUtvandredeIdenter);
        listerMedIdenter.put(GIFTE_IDENTER_I_NORGE, gifteIdenterINorge);
        message.append(" - ")
                .append(GIFTE_IDENTER_I_NORGE)
                .append(": ").append(gifteIdenterINorge.size());

        List<String> singleIdenterINorge = new ArrayList<>();
        singleIdenterINorge.addAll(levendeIdenterINorge);
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

    private IkkeFullfoertBehandlingExceptionsContainer opprettOgFyllIkkeFullfoertBehandlingExceptionContainer(
            IkkeFullfoertBehandlingExceptionsContainer ie,
            Exception e,
            List<Long> ids,
            Endringskoder endringskode) {
        if (ie == null) {
            ie = new IkkeFullfoertBehandlingExceptionsContainer();
        }
        ie.addIds(ids)
                .addMessage(e.getMessage() + " (" + e.getClass().getName() + " - endringskode: " + endringskode.getEndringskode())
                .addCause(e);
        return ie;
    }
}
