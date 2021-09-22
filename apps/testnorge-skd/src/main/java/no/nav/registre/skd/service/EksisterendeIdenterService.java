package no.nav.registre.skd.service;

import static java.util.Objects.isNull;
import static no.nav.registre.skd.service.SyntetiseringService.BRUKTE_IDENTER_I_DENNE_BOLKEN;
import static no.nav.registre.skd.service.SyntetiseringService.FOEDTE_IDENTER;
import static no.nav.registre.skd.service.SyntetiseringService.GIFTE_IDENTER_I_NORGE;
import static no.nav.registre.skd.service.SyntetiseringService.LEVENDE_IDENTER_I_NORGE;
import static no.nav.registre.skd.service.SyntetiseringService.SINGLE_IDENTER_I_NORGE;
import static no.nav.registre.skd.service.utilities.RedigereSkdmeldingerUtility.korrigerUtenFastBosted;
import static no.nav.registre.skd.service.utilities.RedigereSkdmeldingerUtility.opprettKopiAvSkdMelding;
import static no.nav.registre.skd.service.utilities.RedigereSkdmeldingerUtility.putEktefellePartnerFnrInnIMelding;
import static no.nav.registre.skd.service.utilities.RedigereSkdmeldingerUtility.putFnrInnIMelding;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;

import no.nav.registre.skd.consumer.HodejegerenConsumerSkd;
import no.nav.registre.skd.consumer.dto.Relasjon;
import no.nav.registre.skd.consumer.response.RelasjonsResponse;
import no.nav.registre.skd.domain.Endringskoder;
import no.nav.registre.skd.domain.KoderForSivilstand;
import no.nav.registre.skd.exceptions.ManglendeInfoITpsException;
import no.nav.registre.skd.exceptions.ManglerEksisterendeIdentException;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import no.nav.registre.skd.skdmelding.RsMeldingstype1Felter;
import no.nav.testnav.libs.servletcore.util.IdentUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class EksisterendeIdenterService {

    private static final String SKD_MELDINGSNUMMER_FEILMELDING = "Kunne ikke finne ident for SkdMelding med meldingsnummer ";
    private static final String IDENT = "ident";
    private static final String STATSBORGER_NORGE = "NORGE";
    private static final String SIVILSTANDSENDRING_AARSAKSKODE = "85";
    private static final int ANTALL_FORSOEK_PER_AARSAK = 10;
    static final String RELASJON_FAR = "FARA";
    static final String RELASJON_MOR = "MORA";
    static final String DATO_DO = "datoDo";
    static final String FNR_RELASJON = "$..relasjon[?(@.typeRelasjon=='EKTE')].fnrRelasjon";
    static final String SIVILSTAND = "sivilstand";
    static final String STATSBORGER = "statsborger";

    private final HodejegerenConsumerSkd hodejegerenConsumerSkd;

    private final FoedselService foedselService;

    private final Random rand;

    void behandleEksisterendeIdenter(
            List<RsMeldingstype> meldinger,
            Map<String, List<String>> listerMedIdenter,
            Endringskoder endringskode,
            String environment
    ) {
        switch (endringskode) {
            case NAVNEENDRING_FOERSTE:
            case NAVNEENDRING_MELDING:
            case NAVNEENDRING_KORREKSJON:
            case ADRESSEENDRING_UTEN_FLYTTING:
            case ADRESSEKORREKSJON:
            case UTVANDRING:
            case ENDRING_STATSBORGERSKAP_BIBEHOLD:
            case ENDRING_FAMILIENUMMER:
            case ENDRING_FORELDREANSVAR:
            case ENDRING_OPPHOLDSTILLATELSE:
            case ENDRING_POSTADRESSE_TILLEGGSADRESSE:
            case ENDRING_POSTNUMMER_SKOLE_VALG_GRUNNKRETS:
            case KOMMUNEREGULERING:
            case ADRESSEENDRING_GAB:
            case ENDRING_KORREKSJON_FOEDESTED:
            case ENDRING_DUF_NUMMER:
            case FLYTTING_INNEN_KOMMUNEN:
            case UREGISTRERT_PERSON:
            case ANNULERING_FLYTTING_ADRESSEENDRING:
            case INNFLYTTING_ANNEN_KOMMUNE:
                behandleGenerellAarsak(
                        meldinger,
                        listerMedIdenter.get(LEVENDE_IDENTER_I_NORGE),
                        listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN),
                        endringskode,
                        environment
                );
                break;
            case VIGSEL:
                behandleVigsel(
                        meldinger,
                        listerMedIdenter.get(SINGLE_IDENTER_I_NORGE),
                        listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN),
                        endringskode,
                        environment
                );
                break;
            case SEPERASJON:
            case SKILSMISSE:
                behandleSeperasjonSkilsmisse(
                        meldinger,
                        listerMedIdenter.get(GIFTE_IDENTER_I_NORGE),
                        listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN),
                        endringskode,
                        environment
                );
                break;
            case FARSKAP_MEDMORSKAP:
                behandleFarskapMedmorskap(
                        meldinger,
                        listerMedIdenter.get(LEVENDE_IDENTER_I_NORGE),
                        listerMedIdenter.get(FOEDTE_IDENTER),
                        listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN),
                        endringskode,
                        environment
                );
                break;
            case DOEDSMELDING:
                behandleDoedsmelding(
                        meldinger,
                        listerMedIdenter.get(LEVENDE_IDENTER_I_NORGE),
                        listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN),
                        endringskode,
                        environment
                );
                break;
            case KORREKSJON_FAMILIEOPPLYSNINGER:
                break;
            default:
                break;
        }
    }

    void behandleVigsel(
            List<RsMeldingstype> meldinger,
            List<String> singleIdenterINorge,
            List<String> brukteIdenterIDenneBolken,
            Endringskoder endringskode,
            String environment
    ) {
        log.info("Oppretter {} vigsler", meldinger.size());
        List<RsMeldingstype> meldingerForPartnere = new ArrayList<>();

        for (int i = 0; i < meldinger.size(); i++) {
            forFaaIdenterFeilmelding(meldinger, i, singleIdenterINorge.size() - 1, ". For få identer i listen singleIdenterINorge fra TPSF avspillergruppen.");

            Map<String, String> statusQuoIdent = hentIdentMedStatus(singleIdenterINorge, endringskode, environment);
            Map<String, String> statusQuoPartnerIdent = hentIdentMedStatus(singleIdenterINorge, endringskode, environment);

            var ident = statusQuoIdent.get(IDENT);
            var identPartner = statusQuoPartnerIdent.get(IDENT);

            if (ident != null && identPartner != null) {
                putFnrInnIMelding((RsMeldingstype1Felter) meldinger.get(i), ident);
                putEktefellePartnerFnrInnIMelding((RsMeldingstype1Felter) meldinger.get(i), identPartner);
                ((RsMeldingstype1Felter) meldinger.get(i)).setSivilstand(KoderForSivilstand.GIFT.getSivilstandKodeSKD());

                var meldingPartner = opprettKopiAvSkdMelding((RsMeldingstype1Felter) meldinger.get(i), identPartner);
                putEktefellePartnerFnrInnIMelding(meldingPartner, ident);
                meldingPartner.setSivilstand(KoderForSivilstand.GIFT.getSivilstandKodeSKD());
                meldingerForPartnere.add(meldingPartner);

                oppdaterBolk(brukteIdenterIDenneBolken, Arrays.asList(ident, identPartner));
            }
        }
        meldinger.addAll(meldingerForPartnere);
    }

    private Map<String, String> hentIdentMedStatus(List<String> singleIdenterINorge, Endringskoder endringskode, String environment) {
        Map<String, String> statusQuoIdent;
        statusQuoIdent = getIdentWithStatus(singleIdenterINorge, endringskode, environment,
                (Map<String, String> a) -> KoderForSivilstand.GIFT.getAlleSivilstandkodene().contains(a.get(SIVILSTAND))
                        || KoderForSivilstand.SEPARERT.getAlleSivilstandkodene().contains(a.get(SIVILSTAND))
                        || ChronoUnit.YEARS.between(IdentUtil.getFoedselsdatoFraIdent(a.get(IDENT)), LocalDate.now()) < 18);
        return statusQuoIdent;
    }

    void behandleSeperasjonSkilsmisse(
            List<RsMeldingstype> meldinger,
            List<String> gifteIdenterINorge,
            List<String> brukteIdenterIDenneBolken,
            Endringskoder endringskode,
            String environment
    ) {
        List<RsMeldingstype> meldingerForPartnere = new ArrayList<>();

        var antallMeldingerFoerKjoering = meldinger.size();
        var i = 0;
        while (i < antallMeldingerFoerKjoering) {
            forFaaIdenterFeilmelding(meldinger, i, gifteIdenterINorge.size() - 1, ". For få identer i listen gifteIdenterINorge fra TPSF avspillergruppen.");

            var statusQuoIdent = getIdentWithStatus(gifteIdenterINorge, endringskode, environment,
                    (Map<String, String> a) -> !KoderForSivilstand.GIFT.getAlleSivilstandkodene().contains(a.get(SIVILSTAND))
                            && !KoderForSivilstand.SEPARERT.getAlleSivilstandkodene().contains(a.get(SIVILSTAND)));

            var ident = statusQuoIdent.get(IDENT);
            var identPartner = statusQuoIdent.get(FNR_RELASJON);

            Map<String, String> statusQuoPartnerIdent;
            statusQuoPartnerIdent = getStatusQuoPaaIdent(endringskode, environment, identPartner);

            if (statusQuoPartnerIdent.get(SIVILSTAND).equals(statusQuoIdent.get(SIVILSTAND))
                    && statusQuoPartnerIdent.get(FNR_RELASJON).equals(ident)) {
                putFnrInnIMelding((RsMeldingstype1Felter) meldinger.get(i), ident);

                var meldingPartner = opprettKopiAvSkdMelding((RsMeldingstype1Felter) meldinger.get(i), identPartner);

                if (endringskode.getEndringskode().equals(Endringskoder.SEPERASJON.getEndringskode())) {
                    meldingPartner.setSivilstand(KoderForSivilstand.SEPARERT.getSivilstandKodeSKD());
                } else if (endringskode.getEndringskode().equals(Endringskoder.SKILSMISSE.getEndringskode())) {
                    meldingPartner.setSivilstand(KoderForSivilstand.SKILT.getSivilstandKodeSKD());
                }
                ((RsMeldingstype1Felter) meldinger.get(i)).setEktefellePartnerFdato(meldingPartner.getFodselsdato());
                ((RsMeldingstype1Felter) meldinger.get(i)).setEktefellePartnerPnr(meldingPartner.getPersonnummer());
                meldingPartner.setEktefellePartnerFdato(((RsMeldingstype1Felter) meldinger.get(i)).getFodselsdato());
                meldingPartner.setEktefellePartnerPnr(((RsMeldingstype1Felter) meldinger.get(i)).getPersonnummer());

                meldingerForPartnere.add(meldingPartner);

                oppdaterBolk(brukteIdenterIDenneBolken, Arrays.asList(ident, identPartner));
                i++; // Neste melding
            } else {
                log.warn("Korrupte data i TPS - personnummeret eller sivilstanden stemmer ikke for personene med fødselsnumrene: {} og {}",
                        ident, identPartner);
                // Prøver på nytt med å finne et nytt par for samme melding
            }
        }
        meldinger.addAll(meldingerForPartnere);
    }

    void behandleFarskapMedmorskap(
            List<RsMeldingstype> meldinger,
            List<String> levendeIdenterINorge,
            List<String> foedteIdenter,
            List<String> brukteIdenterIDenneBolken,
            Endringskoder endringskode,
            String environment
    ) {
        var antallMeldinger = meldinger.size();
        Map<String, String> barnMedFedre = new HashMap<>();

        for (String foedtIdent : foedteIdenter) {
            var relasjonerTilBarn = hodejegerenConsumerSkd.getRelasjoner(foedtIdent, environment);

            if (relasjonerTilBarn.getRelasjoner().isEmpty()) {
                continue;
            }

            var morFnr = hentMorFnrRelasjon(relasjonerTilBarn);

            if (isNull(morFnr)) {
                continue;
            }

            var farFnr = foedselService.findFar(morFnr, foedtIdent, levendeIdenterINorge, new ArrayList<>());
            if (farFnr != null) {
                barnMedFedre.put(foedtIdent, farFnr);
            }

            if (barnMedFedre.size() >= meldinger.size()) {
                break;
            }
        }

        if (barnMedFedre.isEmpty()) {
            throw new ManglerEksisterendeIdentException("Kunne ikke finne identer for SkdMelding med endringskode "
                    + endringskode.getEndringskode() + ". Ingen identer i TPSF avspillergruppen mangler far til farskapsmelding eller ingen gyldige fedre funnet.");
        }

        if (barnMedFedre.size() < meldinger.size()) {
            log.info("Fant ikke nok barn uten far-relasjon. Oppretter " + barnMedFedre.size() + " farskapsmeldinger.");
            antallMeldinger = barnMedFedre.size();
        }

        oppdaterMeldingerMedFarsFnr(meldinger, barnMedFedre, antallMeldinger, brukteIdenterIDenneBolken);
    }

    private String hentMorFnrRelasjon(RelasjonsResponse relasjonerTilBarn) {
        String morFnr = "";
        for (Relasjon relasjon : relasjonerTilBarn.getRelasjoner()) {

            if (RELASJON_FAR.equals(relasjon.getTypeRelasjon())) {
                return null;
            }
            if (RELASJON_MOR.equals(relasjon.getTypeRelasjon())) {
                morFnr = relasjon.getFnrRelasjon();
            }
        }
        return morFnr;
    }

    void behandleDoedsmelding(
            List<RsMeldingstype> meldinger,
            List<String> levendeIdenterINorge,
            List<String> brukteIdenterIDenneBolken,
            Endringskoder endringskode,
            String environment
    ) {
        List<RsMeldingstype> meldingerForPartnere = new ArrayList<>();

        var antallMeldingerFoerKjoering = meldinger.size();
        for (int i = 0; i < antallMeldingerFoerKjoering; i++) {
            forFaaIdenterFeilmelding(meldinger, i, levendeIdenterINorge.size(), ". For få identer i listen levendeIdenterINorge fra TPSF avspillergruppen.");

            var statusQuoIdent = getIdentWithStatus(levendeIdenterINorge, endringskode, environment,
                    (Map<String, String> a) -> a.get(DATO_DO) != null && !a.get(DATO_DO).isEmpty() || !STATSBORGER_NORGE.equals(a.get(STATSBORGER)));

            var ident = statusQuoIdent.get(IDENT);

            if (KoderForSivilstand.GIFT.getAlleSivilstandkodene().contains(statusQuoIdent.get(SIVILSTAND))) {
                var identPartner = statusQuoIdent.get(FNR_RELASJON);

                if (identPartner.length() != 11) {
                    log.warn("behandleDoedsmelding: Feil på fnr {} fra FNR_RELASJON. Fnr har en lengde på {}. Hopper over sivilstandendringsmelding på partner.",
                            identPartner, identPartner.length());
                    putFnrInnIMelding((RsMeldingstype1Felter) meldinger.get(i), ident);
                    oppdaterBolk(brukteIdenterIDenneBolken, Collections.singletonList(ident));
                } else {
                    var statusQuoPartnerIdent = getStatusQuoPaaIdent(endringskode, environment, identPartner);

                    if (statusQuoPartnerIdent.get(SIVILSTAND).equals(statusQuoIdent.get(SIVILSTAND)) && statusQuoPartnerIdent.get(FNR_RELASJON).equals(ident)) {
                        putFnrInnIMelding((RsMeldingstype1Felter) meldinger.get(i), ident);

                        meldingerForPartnere.add(opprettSivilstandsendringsmelding(meldinger.get(i), identPartner));

                        oppdaterBolk(brukteIdenterIDenneBolken, Arrays.asList(ident, identPartner));
                    }
                }
            } else {
                putFnrInnIMelding((RsMeldingstype1Felter) meldinger.get(i), ident);
                oppdaterBolk(brukteIdenterIDenneBolken, Collections.singletonList(ident));
            }
        }
        meldinger.addAll(meldingerForPartnere);
    }

    private void forFaaIdenterFeilmelding(List<RsMeldingstype> meldinger, int i, int size, String s) {
        if (i >= size) {
            throw new ManglerEksisterendeIdentException(SKD_MELDINGSNUMMER_FEILMELDING
                    + meldinger.get(i).getMeldingsnrHosTpsSynt() + s);
        }
    }

    void behandleGenerellAarsak(
            List<RsMeldingstype> meldinger,
            List<String> levendeIdenterINorge,
            List<String> brukteIdenterIDenneBolken,
            Endringskoder endringskode,
            String environment
    ) {
        for (int i = 0; i < meldinger.size(); i++) {
            forFaaIdenterFeilmelding(meldinger, i, levendeIdenterINorge.size(), ". For få identer i listen levendeIdenterINorge fra TPSF avspillergruppen.");

            var statusQuoIdent = getIdentWithStatus(levendeIdenterINorge, endringskode, environment,
                    (Map<String, String> a) -> a.get(DATO_DO) != null && !a.get(DATO_DO).isEmpty() || !STATSBORGER_NORGE.equals(a.get(STATSBORGER)));

            var ident = statusQuoIdent.get(IDENT);

            if (ident != null) {
                putFnrInnIMelding((RsMeldingstype1Felter) meldinger.get(i), ident);
                if ("UTENFASTBOSTED".equals(((RsMeldingstype1Felter) meldinger.get(i)).getAdresse1())) {
                    korrigerUtenFastBosted((RsMeldingstype1Felter) meldinger.get(i));
                }
                oppdaterBolk(brukteIdenterIDenneBolken, Collections.singletonList(ident));
            }
        }
    }

    private Map<String, String> getIdentWithStatus(
            List<String> identer,
            Endringskoder endringskode,
            String environment,
            Predicate<Map<String, String>> predicate
    ) {
        Map<String, String> statusQuoIdent;
        do {
            statusQuoIdent = findExistingPersonStatusInTps(identer, endringskode, environment);
        } while (predicate.test(statusQuoIdent));
        return statusQuoIdent;
    }

    /**
     * Metoden prøver å hente ut statusQuo på identer. Den prøver inntil den finner noen som eksisterer i TPS (ikke kaster
     * ManglerEksisterendeIdentException) eller antall forsøk overstiger et gitt antall.
     */
    private Map<String, String> findExistingPersonStatusInTps(
            List<String> identer,
            Endringskoder endringskode,
            String environment
    ) {
        Map<String, String> statusQuoIdent;
        var randomIdent = "";
        int randomIndex;

        for (int i = 1; true; i++) {
            if (identer.isEmpty()) {
                throw new ManglerEksisterendeIdentException("Kunne ikke finne ident for SkdMelding. For få identer i " +
                        "listen av identer fra TPSF avspillergruppen.");
            }
            randomIndex = rand.nextInt(identer.size());
            randomIdent = identer.remove(randomIndex);
            try {
                statusQuoIdent = getStatusQuoPaaIdent(endringskode, environment, randomIdent);
                break;
            } catch (ManglendeInfoITpsException | HttpStatusCodeException e) {
                if (i >= ANTALL_FORSOEK_PER_AARSAK) {
                    throw new ManglendeInfoITpsException("Kunne ikke finne ident med gyldig status quo i TPS etter " + i + " forsøk. " +
                            "Status på siste forsøk: " + e.getMessage(), e);
                }
            }
        }
        statusQuoIdent.put(IDENT, randomIdent);
        return statusQuoIdent;
    }

    @Timed(value = "skd.resource.latency", extraTags = {"operation", "hodejegeren"})
    private Map<String, String> getStatusQuoPaaIdent(
            Endringskoder endringskode,
            String environment,
            String fnr
    ) {
        return hodejegerenConsumerSkd.getStatusQuoTilhoerendeEndringskode(endringskode.getEndringskode(), environment, fnr);
    }

    private RsMeldingstype1Felter opprettSivilstandsendringsmelding(
            RsMeldingstype identMelding,
            String identPartner
    ) {
        var melding = RsMeldingstype1Felter.builder()
                .regdatoSivilstand(((RsMeldingstype1Felter) identMelding).getRegDato())
                .fodselsdato(identPartner.substring(0, 6))
                .personnummer(identPartner.substring(6))
                .sivilstand(KoderForSivilstand.ENKE_ENKEMANN.getSivilstandKodeSKD())
                .personkode("1")
                .ektefellePartnerFdato(((RsMeldingstype1Felter) identMelding).getFodselsdato())
                .ektefellePartnerPnr(((RsMeldingstype1Felter) identMelding).getPersonnummer())
                .build();

        melding.setAarsakskode(SIVILSTANDSENDRING_AARSAKSKODE);
        melding.setTranstype("1");
        melding.setMaskindato(identMelding.getMaskindato());
        melding.setMaskintid(identMelding.getMaskintid());
        melding.setRegDato(((RsMeldingstype1Felter) identMelding).getRegDato());
        melding.setStatuskode("1");
        melding.setTildelingskode("0");

        return melding;
    }

    private void oppdaterMeldingerMedFarsFnr(
            List<RsMeldingstype> meldinger,
            Map<String, String> barnMedFedre,
            int antallMeldinger,
            List<String> brukteIdenterIDenneBolken
    ) {
        var meldingIterator = meldinger.iterator();
        var i = 0;
        var barn = new ArrayList<>(barnMedFedre.keySet());
        while (meldingIterator.hasNext()) {
            var melding = meldingIterator.next();

            if (i >= antallMeldinger) {
                meldingIterator.remove();
                break;
            }

            var barnFnr = barn.get(i++);
            var farFnr = barnMedFedre.remove(barnFnr);

            putFnrInnIMelding((RsMeldingstype1Felter) melding, barnFnr);
            ((RsMeldingstype1Felter) melding).setFarsFodselsdato(farFnr.substring(0, 6));
            ((RsMeldingstype1Felter) melding).setFarsPersonnummer(farFnr.substring(6));
            oppdaterBolk(brukteIdenterIDenneBolken, Arrays.asList(farFnr, barnFnr));
        }
    }

    private void oppdaterBolk(
            List<String> brukteIdenterIDenneBolken,
            List<String> identer
    ) {
        brukteIdenterIDenneBolken.addAll(identer);
    }
}