package no.nav.registre.hodejegeren.service;

import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.DATO_DO;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.FNR_RELASJON;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.SIVILSTAND;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.STATSBORGER;
import static no.nav.registre.hodejegeren.service.HodejegerService.BRUKTE_IDENTER_I_DENNE_BOLKEN;
import static no.nav.registre.hodejegeren.service.HodejegerService.GIFTE_IDENTER_I_NORGE;
import static no.nav.registre.hodejegeren.service.HodejegerService.LEVENDE_IDENTER_I_NORGE;
import static no.nav.registre.hodejegeren.service.HodejegerService.SINGLE_IDENTER_I_NORGE;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.hodejegeren.exception.ManglerEksisterendeIdentException;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype1Felter;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class EksisterendeIdenterService {

    private static final String IDENT = "ident";
    private static final String STATSBORGER_NORGE = "NORGE";
    private static final String SIVILSTANDSENDRING_AARSAKSKODE = "85";
    @Autowired
    private EndringskodeTilFeltnavnMapperService endringskodeTilFeltnavnMapperService;
    private Random rand;

    public EksisterendeIdenterService(Random rand) {
        this.rand = rand;
    }

    public static LocalDate getFoedselsdatoFraFnr(String fnr) {
        int pnrAarhundreKode = Integer.parseInt(fnr.substring(6, 9));
        int day = Integer.parseInt(fnr.substring(0, 2));
        int month = Integer.parseInt(fnr.substring(2, 4));
        int year;

        if (pnrAarhundreKode < 500) {
            year = Integer.parseInt(19 + fnr.substring(4, 6));
        } else {
            year = Integer.parseInt(20 + fnr.substring(4, 6));
        }

        return LocalDate.of(year, month, day);
    }

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
            case FOEDSELSNUMMERKORREKSJON:
            case ENDRING_FORELDREANSVAR:
            case ENDRING_OPPHOLDSTILLATELSE:
            case ENDRING_POSTADRESSE_TILLEGGSADRESSE:
            case ENDRING_POSTNUMMER_SKOLE_VALG_GRUNNKRETS:
            case KOMMUNEREGULERING:
            case ADRESSEENDRING_GAB:
            case ENDRING_KORREKSJON_FOEDESTED:
            case ENDRING_DUF_NUMMER:
            case FLYTTING_INNEN_KOMMUNEN:
            case FOEDSELSMELDING:
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
            case INNVANDRING:
            case TILDELING_DNUMMER:
                break;
            default:
                break;
        }
    }

    public void behandleVigsel(List<RsMeldingstype> meldinger, List<String> singleIdenterINorge, List<String> brukteIdenterIDenneBolken,
            Endringskoder endringskode, String environment) {
        List<RsMeldingstype> meldingerForPartnere = new ArrayList<>();

        for (int i = 0; i < meldinger.size(); i++) {
            if (i >= singleIdenterINorge.size() - 2) {
                throw new ManglerEksisterendeIdentException("Kunne ikke finne ident for SkdMelding med meldingsnummer "
                        + meldinger.get(i).getMeldingsnrHosTpsSynt() + ". For få identer i listen singleIdenterINorge.");
            }

            Map<String, String> statusQuoIdent;
            statusQuoIdent = getIdentWithStatus(singleIdenterINorge, endringskode, environment,
                    (Map<String, String> a) -> KoderForSivilstand.GIFT.getSivilstandKode().equals(a.get(SIVILSTAND))
                            || KoderForSivilstand.SEPARERT.getSivilstandKode().equals(a.get(SIVILSTAND))
                            || ChronoUnit.YEARS.between(getFoedselsdatoFraFnr(a.get(IDENT)), LocalDate.now()) < 18);

            Map<String, String> statusQuoPartnerIdent;
            statusQuoPartnerIdent = getIdentWithStatus(singleIdenterINorge, endringskode, environment,
                    (Map<String, String> a) -> KoderForSivilstand.GIFT.getSivilstandKode().equals(a.get(SIVILSTAND))
                            || KoderForSivilstand.SEPARERT.getSivilstandKode().equals(a.get(SIVILSTAND))
                            || ChronoUnit.YEARS.between(getFoedselsdatoFraFnr(a.get(IDENT)), LocalDate.now()) < 18);

            String ident = statusQuoIdent.get(IDENT);
            String identPartner = statusQuoPartnerIdent.get(IDENT);

            if (ident != null && identPartner != null) {
                putFnrInnIMelding((RsMeldingstype1Felter) meldinger.get(i), ident);
                putEktefellePartnerFnrInnIMelding((RsMeldingstype1Felter) meldinger.get(i), identPartner);

                RsMeldingstype melding = opprettKopiAvSkdMelding((RsMeldingstype1Felter) meldinger.get(i), identPartner);
                putEktefellePartnerFnrInnIMelding(((RsMeldingstype1Felter) melding), ident);
                meldingerForPartnere.add(melding);

                oppdaterBolk(brukteIdenterIDenneBolken, Arrays.asList(ident, identPartner));
            }
        }
        meldinger.addAll(meldingerForPartnere);
    }

    public void behandleSeperasjonSkilsmisse(List<RsMeldingstype> meldinger, List<String> gifteIdenterINorge, List<String> brukteIdenterIDenneBolken,
            Endringskoder endringskoder, String environment) {
        List<RsMeldingstype> meldingerForPartnere = new ArrayList<>();

        int antallMeldingerFoerKjoering = meldinger.size();
        for (int i = 0; i < antallMeldingerFoerKjoering; i++) {
            if (i >= gifteIdenterINorge.size() - 1) {
                throw new ManglerEksisterendeIdentException("Kunne ikke finne ident for SkdMelding med meldingsnummer "
                        + meldinger.get(i).getMeldingsnrHosTpsSynt() + ". For få identer i listen gifteIdenterINorge fra TPSF avspillergruppen.");
            }

            Map<String, String> statusQuoIdent = getIdentWithStatus(gifteIdenterINorge, endringskoder, environment,
                    (Map<String, String> a) -> !KoderForSivilstand.GIFT.getSivilstandKode().equals(a.get(SIVILSTAND)));

            String ident = statusQuoIdent.get(IDENT);
            Map<String, String> statusQuoPartnerIdent;
            String identPartner;

            if (ident != null) {
                identPartner = statusQuoIdent.get(FNR_RELASJON);

                statusQuoPartnerIdent = getStatusQuoPaaIdent(endringskoder, environment, identPartner);

                if (statusQuoPartnerIdent.get(SIVILSTAND).equals(statusQuoIdent.get(SIVILSTAND))
                        && statusQuoPartnerIdent.get(FNR_RELASJON).equals(ident)) {
                    putFnrInnIMelding((RsMeldingstype1Felter) meldinger.get(i), ident);

                    RsMeldingstype melding = opprettKopiAvSkdMelding((RsMeldingstype1Felter) meldinger.get(i), identPartner);
                    meldingerForPartnere.add(melding);

                    oppdaterBolk(brukteIdenterIDenneBolken, Arrays.asList(ident, identPartner));

                } else {
                    log.warn("Korrupte data i TPS - personnummeret eller sivilstanden stemmer ikke for personene med fødselsnumrene: "
                            + ident + " og " +identPartner);
                    i--; //Prøver på nytt med et nytt par for samme melding
                }
            } else {
                // fant ikke ident
            }
        }
        meldinger.addAll(meldingerForPartnere);
    }

    public void behandleDoedsmelding(List<RsMeldingstype> meldinger, List<String> levendeIdenterINorge, List<String> brukteIdenterIDenneBolken,
            Endringskoder endringskode, String environment) {
        List<RsMeldingstype> meldingerForPartnere = new ArrayList<>();

        int antallMeldingerFoerKjoering = meldinger.size();
        for (int i = 0; i < antallMeldingerFoerKjoering; i++) {
            if (i >= levendeIdenterINorge.size() - 1) {
                throw new ManglerEksisterendeIdentException("Kunne ikke finne ident for SkdMelding med meldingsnummer "
                        + meldinger.get(i).getMeldingsnrHosTpsSynt() + ". For få identer i listen levendeIdenterINorge.");
            }

            Map<String, String> statusQuoIdent = getIdentWithStatus(levendeIdenterINorge, endringskode, environment,
                    (Map<String, String> a) -> (a.get(DATO_DO) != null && !a.get(DATO_DO).isEmpty()) || !STATSBORGER_NORGE.equals(a.get(STATSBORGER)));

            String ident = statusQuoIdent.get(IDENT);
            Map<String, String> statusQuoPartnerIdent;
            String identPartner;

            if (statusQuoIdent.get(SIVILSTAND).equals(KoderForSivilstand.GIFT.getSivilstandKode())) {
                identPartner = statusQuoIdent.get(FNR_RELASJON);

                statusQuoPartnerIdent = getStatusQuoPaaIdent(endringskode, environment, identPartner);

                if (statusQuoPartnerIdent.get(SIVILSTAND).equals(statusQuoIdent.get(SIVILSTAND))) {
                    if (statusQuoPartnerIdent.get(FNR_RELASJON).equals(ident)) {
                        putFnrInnIMelding((RsMeldingstype1Felter) meldinger.get(i), ident);

                        meldingerForPartnere.add(opprettSivilstandsendringsmelding(ident, identPartner));

                        oppdaterBolk(brukteIdenterIDenneBolken, Arrays.asList(ident, identPartner));
                    } else {
                        // personnummer i fnrRelasjon til partner matcher ikke
                    }
                } else {
                    // ulik sivilstand på identene
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
            if (i >= levendeIdenterINorge.size() - 1) {
                throw new ManglerEksisterendeIdentException("Kunne ikke finne ident for SkdMelding med meldingsnummer "
                        + meldinger.get(i).getMeldingsnrHosTpsSynt() + ". For få identer i listen levendeIdenterINorge.");
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

    private Map<String, String> getIdentWithStatus(List<String> identer, Endringskoder endringskode, String environment, Predicate<Map<String, String>> predicate) {
        Map<String, String> statusQuoIdent;
        String randomIdent;
        do {
            int randomIndex = rand.nextInt(identer.size());
            randomIdent = identer.remove(randomIndex);
            statusQuoIdent = getStatusQuoPaaIdent(endringskode, environment, randomIdent);
            statusQuoIdent.put(IDENT, randomIdent);
        }
        while (predicate.test(statusQuoIdent));
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

    private void putFnrInnIMelding(RsMeldingstype1Felter melding, String fnr) {
        melding.setFodselsdato(fnr.substring(0, 6));
        melding.setPersonnummer(fnr.substring(6));
    }

    private void putEktefellePartnerFnrInnIMelding(RsMeldingstype1Felter melding, String identPartner) {
        melding.setEktefellePartnerFdato(identPartner.substring(0, 6));
        melding.setEktefellePartnerPnr(identPartner.substring(6));
    }

    private RsMeldingstype1Felter opprettKopiAvSkdMelding(RsMeldingstype1Felter originalMelding, String fnr) {
        RsMeldingstype1Felter kopi = originalMelding.toBuilder()
                .fodselsdato(fnr.substring(0, 6))
                .personnummer(fnr.substring(6)).build();
        kopi.setTranstype(originalMelding.getTranstype());
        kopi.setMaskindato(originalMelding.getMaskindato());
        kopi.setMaskintid(originalMelding.getMaskintid());
        kopi.setAarsakskode(originalMelding.getAarsakskode());
        kopi.setSekvensnr(originalMelding.getSekvensnr());
        return kopi;
    }

    private RsMeldingstype1Felter opprettSivilstandsendringsmelding(String ident, String identPartner) {
        RsMeldingstype1Felter melding = RsMeldingstype1Felter.builder()
                .regdatoSivilstand(LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("ddMMyy")))
                .fodselsdato(identPartner.substring(0, 6))
                .personnummer(identPartner.substring(6))
                .sivilstand(KoderForSivilstand.ENKE_ENKEMANN.getSivilstandKode())
                .personkode("1")
                .ektefellePartnerFdato(ident.substring(0, 6))
                .ektefellePartnerPnr(ident.substring(6))
                .build();

        melding.setAarsakskode(SIVILSTANDSENDRING_AARSAKSKODE);
        melding.setMaskindato(LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyy")));
        melding.setMaskintid(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")));

        return melding;
    }

    private void oppdaterBolk(List<String> brukteIdenterIDenneBolken, List<String> identer) {
        brukteIdenterIDenneBolken.addAll(identer);
    }
}