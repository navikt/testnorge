package no.nav.registre.hodejegeren.service;

import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.DATO_DO;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.FNR_RELASJON;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.SIVILSTAND;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.STATSBORGER;
import static no.nav.registre.hodejegeren.service.HodejegerService.BRUKTE_IDENTER_I_DENNE_BOLKEN;
import static no.nav.registre.hodejegeren.service.HodejegerService.GIFTE_IDENTER_I_NORGE;
import static no.nav.registre.hodejegeren.service.HodejegerService.LEVENDE_IDENTER_I_NORGE;
import static no.nav.registre.hodejegeren.service.HodejegerService.SINGLE_IDENTER_I_NORGE;
import static no.nav.registre.hodejegeren.service.utilities.IdentUtility.getFoedselsdatoFraFnr;
import static no.nav.registre.hodejegeren.service.utilities.RedigereSkdmeldingerUtility.opprettKopiAvSkdMelding;
import static no.nav.registre.hodejegeren.service.utilities.RedigereSkdmeldingerUtility.putEktefellePartnerFnrInnIMelding;
import static no.nav.registre.hodejegeren.service.utilities.RedigereSkdmeldingerUtility.putFnrInnIMelding;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;

import no.nav.registre.hodejegeren.exception.ManglendeInfoITpsException;
import no.nav.registre.hodejegeren.exception.ManglerEksisterendeIdentException;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype1Felter;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class EksisterendeIdenterSkdService {

    private static final String IDENT = "ident";
    private static final String STATSBORGER_NORGE = "NORGE";
    private static final String SIVILSTANDSENDRING_AARSAKSKODE = "85";
    private static final int ANTALL_FORSOEK_PER_AARSAK = 3;

    @Autowired
    private EndringskodeTilFeltnavnMapperService endringskodeTilFeltnavnMapperService;
    @Autowired
    private Random rand;

    public void behandleEksisterendeIdenter(List<RsMeldingstype> meldinger, Map<String, List<String>> listerMedIdenter,
            Endringskoder endringskode, String environment) {

        switch (endringskode) {
        case NAVNEENDRING_FOERSTE:
        case NAVNEENDRING_MELDING:
        case NAVNEENDRING_KORREKSJON:
        case ADRESSEENDRING_UTEN_FLYTTING:
        case ADRESSEKORREKSJON:
        case UTVANDRING:
        case FARSKAP_MEDMORSKAP:
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
            behandleGenerellAarsak(meldinger, listerMedIdenter.get(LEVENDE_IDENTER_I_NORGE), listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN),
                    endringskode, environment);
            break;
        case VIGSEL:
            behandleVigsel(meldinger, listerMedIdenter.get(SINGLE_IDENTER_I_NORGE), listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN),
                    endringskode, environment);
            break;
        case SEPERASJON:
        case SKILSMISSE:
            behandleSeperasjonSkilsmisse(meldinger, listerMedIdenter.get(GIFTE_IDENTER_I_NORGE), listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN),
                    endringskode, environment);
            break;
        case DOEDSMELDING:
            behandleDoedsmelding(meldinger, listerMedIdenter.get(LEVENDE_IDENTER_I_NORGE), listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN),
                    endringskode, environment);
            break;
        case KORREKSJON_FAMILIEOPPLYSNINGER:
            break;
        default:
            break;
        }
    }

    public void behandleVigsel(List<RsMeldingstype> meldinger, List<String> singleIdenterINorge, List<String> brukteIdenterIDenneBolken,
            Endringskoder endringskode, String environment) {
        List<RsMeldingstype> meldingerForPartnere = new ArrayList<>();

        for (int i = 0; i < meldinger.size(); i++) {
            if (i >= singleIdenterINorge.size() - 1) {
                throw new ManglerEksisterendeIdentException("Kunne ikke finne ident for SkdMelding med meldingsnummer "
                        + meldinger.get(i).getMeldingsnrHosTpsSynt() + ". For få identer i listen singleIdenterINorge fra TPSF avspillergruppen.");
            }

            Map<String, String> statusQuoIdent;
            statusQuoIdent = getIdentWithStatus(singleIdenterINorge, endringskode, environment,
                    (Map<String, String> a) -> KoderForSivilstand.GIFT.getAlleSivilstandkodene().contains(a.get(SIVILSTAND))
                            || KoderForSivilstand.SEPARERT.getAlleSivilstandkodene().contains(a.get(SIVILSTAND))
                            || ChronoUnit.YEARS.between(getFoedselsdatoFraFnr(a.get(IDENT)), LocalDate.now()) < 18);

            Map<String, String> statusQuoPartnerIdent;
            statusQuoPartnerIdent = getIdentWithStatus(singleIdenterINorge, endringskode, environment,
                    (Map<String, String> a) -> KoderForSivilstand.GIFT.getAlleSivilstandkodene().contains(a.get(SIVILSTAND))
                            || KoderForSivilstand.SEPARERT.getAlleSivilstandkodene().contains(a.get(SIVILSTAND))
                            || ChronoUnit.YEARS.between(getFoedselsdatoFraFnr(a.get(IDENT)), LocalDate.now()) < 18);

            String ident = statusQuoIdent.get(IDENT);
            String identPartner = statusQuoPartnerIdent.get(IDENT);

            if (ident != null && identPartner != null) {
                putFnrInnIMelding((RsMeldingstype1Felter) meldinger.get(i), ident);
                putEktefellePartnerFnrInnIMelding((RsMeldingstype1Felter) meldinger.get(i), identPartner);
                ((RsMeldingstype1Felter) meldinger.get(i)).setSivilstand(KoderForSivilstand.GIFT.getSivilstandKodeSKD());

                RsMeldingstype1Felter meldingPartner = opprettKopiAvSkdMelding((RsMeldingstype1Felter) meldinger.get(i), identPartner);
                putEktefellePartnerFnrInnIMelding(meldingPartner, ident);
                meldingPartner.setSivilstand(KoderForSivilstand.GIFT.getSivilstandKodeSKD());
                meldingerForPartnere.add(meldingPartner);

                oppdaterBolk(brukteIdenterIDenneBolken, Arrays.asList(ident, identPartner));
            }
        }
        meldinger.addAll(meldingerForPartnere);
    }

    public void behandleSeperasjonSkilsmisse(List<RsMeldingstype> meldinger, List<String> gifteIdenterINorge, List<String> brukteIdenterIDenneBolken,
            Endringskoder endringskode, String environment) {
        List<RsMeldingstype> meldingerForPartnere = new ArrayList<>();

        int antallMeldingerFoerKjoering = meldinger.size();
        int i = 0;
        while (i < antallMeldingerFoerKjoering) {
            if (i >= gifteIdenterINorge.size() - 1) {
                throw new ManglerEksisterendeIdentException("Kunne ikke finne ident for SkdMelding med meldingsnummer "
                        + meldinger.get(i).getMeldingsnrHosTpsSynt() + ". For få identer i listen gifteIdenterINorge fra TPSF avspillergruppen.");
            }

            Map<String, String> statusQuoIdent = getIdentWithStatus(gifteIdenterINorge, endringskode, environment,
                    (Map<String, String> a) -> !KoderForSivilstand.GIFT.getAlleSivilstandkodene().contains(a.get(SIVILSTAND))
                            && !KoderForSivilstand.SEPARERT.getAlleSivilstandkodene().contains(a.get(SIVILSTAND)));

            String ident = statusQuoIdent.get(IDENT);
            Map<String, String> statusQuoPartnerIdent;
            String identPartner;

            identPartner = statusQuoIdent.get(FNR_RELASJON);
            if (identPartner.length() != 11) { // TODO - Fjerne denne når feilen med tomme fnr er oppdaget/fikset
                log.error("behandleSeperasjonSkilsmisse: Feil på fnr {} fra FNR_RELASJON. Fnr har en lengde på {}", identPartner, identPartner.length());
            }

            statusQuoPartnerIdent = getStatusQuoPaaIdent(endringskode, environment, identPartner);

            if (statusQuoPartnerIdent.get(SIVILSTAND).equals(statusQuoIdent.get(SIVILSTAND))
                    && statusQuoPartnerIdent.get(FNR_RELASJON).equals(ident)) {
                putFnrInnIMelding((RsMeldingstype1Felter) meldinger.get(i), ident);

                RsMeldingstype1Felter meldingPartner = opprettKopiAvSkdMelding((RsMeldingstype1Felter) meldinger.get(i), identPartner);

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

    public void behandleDoedsmelding(List<RsMeldingstype> meldinger, List<String> levendeIdenterINorge, List<String> brukteIdenterIDenneBolken,
            Endringskoder endringskode, String environment) {
        List<RsMeldingstype> meldingerForPartnere = new ArrayList<>();

        int antallMeldingerFoerKjoering = meldinger.size();
        for (int i = 0; i < antallMeldingerFoerKjoering; i++) {
            if (i >= levendeIdenterINorge.size()) {
                throw new ManglerEksisterendeIdentException("Kunne ikke finne ident for SkdMelding med meldingsnummer "
                        + meldinger.get(i).getMeldingsnrHosTpsSynt() + ". For få identer i listen levendeIdenterINorge fra TPSF avspillergruppen.");
            }

            Map<String, String> statusQuoIdent = getIdentWithStatus(levendeIdenterINorge, endringskode, environment,
                    (Map<String, String> a) -> (a.get(DATO_DO) != null && !a.get(DATO_DO).isEmpty()) || !STATSBORGER_NORGE.equals(a.get(STATSBORGER)));

            String ident = statusQuoIdent.get(IDENT);
            Map<String, String> statusQuoPartnerIdent;
            String identPartner;

            if (KoderForSivilstand.GIFT.getAlleSivilstandkodene().contains(statusQuoIdent.get(SIVILSTAND))) {
                identPartner = statusQuoIdent.get(FNR_RELASJON);

                if (identPartner.length() != 11) { // TODO - Fjerne denne når feilen med tomme fnr er oppdaget/fikset
                    log.warn("behandleDoedsmelding: Feil på fnr {} fra FNR_RELASJON. Fnr har en lengde på {}. Hopper over sivilstandendringsmelding på partner.",
                            identPartner, identPartner.length());
                    putFnrInnIMelding((RsMeldingstype1Felter) meldinger.get(i), ident);
                    oppdaterBolk(brukteIdenterIDenneBolken, Arrays.asList(ident));
                } else {
                    statusQuoPartnerIdent = getStatusQuoPaaIdent(endringskode, environment, identPartner);

                    if (statusQuoPartnerIdent.get(SIVILSTAND).equals(statusQuoIdent.get(SIVILSTAND))) {
                        if (statusQuoPartnerIdent.get(FNR_RELASJON).equals(ident)) {
                            putFnrInnIMelding((RsMeldingstype1Felter) meldinger.get(i), ident);

                            meldingerForPartnere.add(opprettSivilstandsendringsmelding(meldinger.get(i), identPartner));

                            oppdaterBolk(brukteIdenterIDenneBolken, Arrays.asList(ident, identPartner));
                        } else {
                            // personnummer i fnrRelasjon til partner matcher ikke
                        }
                    } else {
                        // ulik sivilstand på identene
                    }
                }
            } else {
                putFnrInnIMelding((RsMeldingstype1Felter) meldinger.get(i), ident);
                oppdaterBolk(brukteIdenterIDenneBolken, Arrays.asList(ident));
            }
        }
        meldinger.addAll(meldingerForPartnere);
    }

    public void behandleGenerellAarsak(List<RsMeldingstype> meldinger, List<String> levendeIdenterINorge, List<String> brukteIdenterIDenneBolken,
            Endringskoder endringskode, String environment) {
        for (int i = 0; i < meldinger.size(); i++) {
            if (i >= levendeIdenterINorge.size()) {
                throw new ManglerEksisterendeIdentException("Kunne ikke finne ident for SkdMelding med meldingsnummer "
                        + meldinger.get(i).getMeldingsnrHosTpsSynt() + ". For få identer i listen levendeIdenterINorge fra TPSF avspillergruppen.");
            }

            Map<String, String> statusQuoIdent = getIdentWithStatus(levendeIdenterINorge, endringskode, environment,
                    (Map<String, String> a) -> (a.get(DATO_DO) != null && !a.get(DATO_DO).isEmpty()) || !STATSBORGER_NORGE.equals(a.get(STATSBORGER)));

            String ident = statusQuoIdent.get(IDENT);

            if (ident != null) {
                putFnrInnIMelding((RsMeldingstype1Felter) meldinger.get(i), ident);
                oppdaterBolk(brukteIdenterIDenneBolken, Arrays.asList(ident));
            }
        }
    }

    private Map<String, String> getIdentWithStatus(List<String> identer, Endringskoder endringskode, String environment,
            Predicate<Map<String, String>> predicate) {
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
    private Map<String, String> findExistingPersonStatusInTps(List<String> identer, Endringskoder endringskode, String environment) {
        Map<String, String> statusQuoIdent = new HashMap<>();
        String randomIdent = "";
        int randomIndex;

        for (int i = 1; i <= ANTALL_FORSOEK_PER_AARSAK; i++) {
            if (identer.isEmpty()) {
                throw new ManglerEksisterendeIdentException("Kunne ikke finne ident for SkdMelding. For få identer i " +
                        "listen av identer fra TPSF avspillergruppen.");
            }
            randomIndex = rand.nextInt(identer.size());
            randomIdent = identer.remove(randomIndex);
            if (randomIdent.length() != 11) { // TODO - Fjerne denne når feilen med tomme fnr er oppdaget/fikset
                log.error("findExistingPersonStatusInTps: Feil på fnr {} i randomIdent. Fnr har en lengde på {}", randomIdent, randomIdent.length());
            }
            try {
                statusQuoIdent = getStatusQuoPaaIdent(endringskode, environment, randomIdent);
                break;
            } catch (ManglendeInfoITpsException e) {
                if (i >= ANTALL_FORSOEK_PER_AARSAK) {
                    throw new ManglendeInfoITpsException("Kunne ikke finne ident med gyldig status quo i TPS etter " + i + " forsøk. " +
                            "Status på siste forsøk: " + e.getMessage(), e);
                }
            }
        }
        statusQuoIdent.put(IDENT, randomIdent);
        return statusQuoIdent;
    }

    private Map<String, String> getStatusQuoPaaIdent(Endringskoder endringskode, String environment, String fnr) {
        Map<String, String> statusQuoFraAarsakskode = new HashMap<>();

        try {
            statusQuoFraAarsakskode.putAll(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(
                    endringskode, environment, fnr));
        } catch (IOException e) {
            log.error("Kunne ikke finne status quo for ident {} ", fnr);
        }

        return statusQuoFraAarsakskode;
    }

    private RsMeldingstype1Felter opprettSivilstandsendringsmelding(RsMeldingstype identMelding, String identPartner) {
        RsMeldingstype1Felter melding = RsMeldingstype1Felter.builder()
                .regdatoSivilstand(((RsMeldingstype1Felter) identMelding).getRegDato())
                .fodselsdato(identPartner.substring(0, 6))
                .personnummer(identPartner.substring(6))
                .sivilstand(KoderForSivilstand.ENKE_ENKEMANN.getSivilstandKodeSKD())
                .personkode("1")
                .ektefellePartnerFdato(((RsMeldingstype1Felter) identMelding).getFodselsdato())
                .ektefellePartnerPnr(((RsMeldingstype1Felter) identMelding).getPersonnummer())
                .build();

        melding.setAarsakskode(SIVILSTANDSENDRING_AARSAKSKODE);
        melding.setMaskindato(identMelding.getMaskindato());
        melding.setMaskintid(identMelding.getMaskintid());
        melding.setRegDato(((RsMeldingstype1Felter) identMelding).getRegDato());
        melding.setStatuskode("1");
        melding.setTildelingskode("0");

        return melding;
    }

    private void oppdaterBolk(List<String> brukteIdenterIDenneBolken, List<String> identer) {
        brukteIdenterIDenneBolken.addAll(identer);
    }
}