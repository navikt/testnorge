package no.nav.registre.hodejegeren.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype1Felter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;

import static no.nav.registre.hodejegeren.service.AarsakskodeTilFeltnavnMapperService.*;
import static no.nav.registre.hodejegeren.service.HodejegerService.*;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class EksisterendeIdenterService {

    @Autowired
    private AarsakskodeTilFeltnavnMapperService aarsakskodeTilFeltnavnMapperService;

    private static final String IDENT = "ident";
    private Random rand;

    public EksisterendeIdenterService(Random rand) {
        this.rand = rand;
    }

    public void behandleEksisterendeIdenter(List<RsMeldingstype> meldinger, Map<String, List<String>> listerMedIdenter, String aarsakskode,
                                            Map<String, Integer> antallMeldingerPerAarsakskode) {

        AarsakskoderTrans1 aarsakskodeEnum = findEnumOfAarsakskode(aarsakskode);
        switch (aarsakskodeEnum) {
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
                        aarsakskode, antallMeldingerPerAarsakskode);
                break;
            case VIGSEL:
                behandleVigsel(meldinger, listerMedIdenter.get(SINGLE_IDENTER_I_NORGE), listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN),
                        aarsakskode, antallMeldingerPerAarsakskode);
                break;
            case SEPERASJON:
            case SKILSMISSE:
            case SIVILSTANDSENDRING:
                behandleSeperasjonSkilsmisse(meldinger, listerMedIdenter.get(GIFTE_IDENTER_I_NORGE), listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN),
                        aarsakskode, antallMeldingerPerAarsakskode);
                break;
            case DOEDSMELDING:
                behandleDoedsmelding(meldinger, listerMedIdenter.get(LEVENDE_IDENTER_I_NORGE), listerMedIdenter.get(BRUKTE_IDENTER_I_DENNE_BOLKEN),
                        aarsakskode, antallMeldingerPerAarsakskode);
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
                               String aarsakskode, Map<String, Integer> antallMeldingerPerAarsakskode) {

        for (int i = 0; i < antallMeldingerPerAarsakskode.get(aarsakskode); i++) {
            if (i >= singleIdenterINorge.size() - 2) {
                // ikke nok identer i singleIdenter-liste
                break;
            }

            if (meldinger.get(i) == null) {
                // fant ikke gyldig skdmelding på index i
                continue;
            }

            Map<String, String> statusQuoFraAarsakskodeIdent = getIdentWithStatus(singleIdenterINorge, aarsakskode,
                    (Map<String, String> a) -> a.get(SIVILSTAND).equals(KoderForSivilstand.GIFT.getSivilstandKode())
                            || a.get(SIVILSTAND).equals(KoderForSivilstand.SEPARERT.getSivilstandKode()));

            Map<String, String> statusQuoFraAarsakskodeIdentPartner = getIdentWithStatus(singleIdenterINorge, aarsakskode,
                    (Map<String, String> a) -> a.get(SIVILSTAND).equals(KoderForSivilstand.GIFT.getSivilstandKode())
                            || a.get(SIVILSTAND).equals(KoderForSivilstand.SEPARERT.getSivilstandKode()));

            String ident = statusQuoFraAarsakskodeIdent.get(IDENT);
            String identPartner = statusQuoFraAarsakskodeIdentPartner.get(IDENT);

            if (ident != null && identPartner != null) {
                putFnrInnIMelding(meldinger.get(i), ident);
                ((RsMeldingstype1Felter) meldinger.get(i)).setEktefellePartnerFdato(identPartner.substring(0, 6));
                ((RsMeldingstype1Felter) meldinger.get(i)).setEktefellePartnerPnr(identPartner.substring(6));

                RsMeldingstype melding = putIdentInnINyMelding(meldinger, identPartner);
                ((RsMeldingstype1Felter) melding).setEktefellePartnerFdato(ident.substring(0, 6));
                ((RsMeldingstype1Felter) melding).setEktefellePartnerPnr(ident.substring(6));

                oppdaterBolk(brukteIdenterIDenneBolken, Arrays.asList(ident, identPartner));
            }
        }
    }

    public void behandleSeperasjonSkilsmisse(List<RsMeldingstype> meldinger, List<String> gifteIdenterINorge, List<String> brukteIdenterIDenneBolken,
                                             String aarsakskode, Map<String, Integer> antallMeldingerPerAarsakskode) {
        for (int i = 0; i < antallMeldingerPerAarsakskode.get(aarsakskode); i++) {
            if (meldinger.get(i) == null) {
                // fant ikke gyldig skdmelding på index i
                continue;
            }

            Map<String, String> statusQuoFraAarsakskodeIdent = getIdentWithStatus(gifteIdenterINorge, aarsakskode,
                    (Map<String, String> a) -> !a.get(SIVILSTAND).equals(KoderForSivilstand.GIFT.getSivilstandKode()));

            String ident = statusQuoFraAarsakskodeIdent.get(IDENT);
            Map<String, String> statusQuoFraAarsakskodeIdentPartner;
            String identPartner;

            if (ident != null && statusQuoFraAarsakskodeIdent != null) {
                identPartner = statusQuoFraAarsakskodeIdent.get(FNR_RELASJON);

                statusQuoFraAarsakskodeIdentPartner = getStatusQuoPaaIdent(aarsakskode, identPartner);

                if (statusQuoFraAarsakskodeIdentPartner.get(SIVILSTAND).equals(statusQuoFraAarsakskodeIdent.get(SIVILSTAND))) {
                    if (statusQuoFraAarsakskodeIdentPartner.get(FNR_RELASJON).equals(ident)) {
                        putFnrInnIMelding(meldinger.get(i), ident);

                        putIdentInnINyMelding(meldinger, identPartner);

                        oppdaterBolk(brukteIdenterIDenneBolken, Arrays.asList(ident, identPartner));
                    } else {
                        // personnummer i fnrRelasjon til partner matcher ikke
                    }
                } else {
                    // ulik sivilstand på identene
                }
            } else {
                // fant ikke ident
            }
        }
    }

    public void behandleDoedsmelding(List<RsMeldingstype> meldinger, List<String> levendeIdenterINorge, List<String> brukteIdenterIDenneBolken,
                                     String aarsakskode, Map<String, Integer> antallMeldingerPerAarsakskode) {
        for (int i = 0; i < antallMeldingerPerAarsakskode.get(aarsakskode); i++) {

            if (meldinger.get(i) == null) {
                // fant ikke gyldig skdmelding på index i
                continue;
            }

            Map<String, String> statusQuoFraAarsakskodeIdent = getIdentWithStatus(levendeIdenterINorge, aarsakskode,
                    (Map<String, String> a) -> !a.get(DATO_DO).isEmpty() || !a.get(STATSBORGER).equals("NORGE"));

            String ident = statusQuoFraAarsakskodeIdent.get(IDENT);
            Map<String, String> statusQuoFraAarsakskodeIdentPartner;
            String identPartner;

            if (statusQuoFraAarsakskodeIdent != null) {
                if (statusQuoFraAarsakskodeIdent.get(SIVILSTAND).equals(KoderForSivilstand.GIFT.getSivilstandKode())) {
                    identPartner = statusQuoFraAarsakskodeIdent.get(FNR_RELASJON);

                    statusQuoFraAarsakskodeIdentPartner = getStatusQuoPaaIdent(aarsakskode, identPartner);

                    if (statusQuoFraAarsakskodeIdentPartner.get(SIVILSTAND).equals(statusQuoFraAarsakskodeIdent.get(SIVILSTAND))) {
                        if (statusQuoFraAarsakskodeIdentPartner.get(FNR_RELASJON).equals(ident)) {
                            putFnrInnIMelding(meldinger.get(i), ident);

                            RsMeldingstype melding = new RsMeldingstype1Felter();
                            melding.setAarsakskode(AarsakskoderTrans1.SIVILSTANDSENDRING.getAarsakskode());
                            melding.setMaskindato(LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyy")));
                            melding.setMaskintid(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")));

                            ((RsMeldingstype1Felter) melding).setRegdatoSivilstand(LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyy")));
                            ((RsMeldingstype1Felter) melding).setFodselsdato(identPartner.substring(0, 6));
                            ((RsMeldingstype1Felter) melding).setPersonnummer(identPartner.substring(6));
                            ((RsMeldingstype1Felter) melding).setSivilstand(KoderForSivilstand.ENKE_ENKEMANN.getSivilstandKode());
                            ((RsMeldingstype1Felter) melding).setPersonkode("1");
                            ((RsMeldingstype1Felter) melding).setEktefellePartnerFdato(ident.substring(0, 6));
                            ((RsMeldingstype1Felter) melding).setEktefellePartnerPnr(ident.substring(6));

                            meldinger.add(melding);

                            oppdaterBolk(brukteIdenterIDenneBolken, Arrays.asList(ident, identPartner));
                        } else {
                            // personnummer i fnrRelasjon til partner matcher ikke
                        }
                    } else {
                        // ulik sivilstand på identene
                    }
                } else {
                    putFnrInnIMelding(meldinger.get(i), ident);
                    oppdaterBolk(brukteIdenterIDenneBolken, Arrays.asList(ident));
                }
            } else {
                // fant ikke ident
            }
        }
    }


    public void behandleGenerellAarsak(List<RsMeldingstype> meldinger, List<String> levendeIdenterINorge, List<String> brukteIdenterIDenneBolken,
                                       String aarsakskode, Map<String, Integer> antallMeldingerPerAarsakskode) {
        for (int i = 0; i < antallMeldingerPerAarsakskode.get(aarsakskode); i++) {

            if (meldinger.get(i) == null) {
                // fant ikke gyldig skdmelding på index i
                continue;
            }

            Map<String, String> statusQuoFraAarsakskodeIdent = getIdentWithStatus(levendeIdenterINorge, aarsakskode,
                    (Map<String, String> a) -> !a.get(DATO_DO).isEmpty() || !a.get(STATSBORGER).equals("NORGE"));

            String ident = statusQuoFraAarsakskodeIdent.get(IDENT);

            if (ident != null) {
                putFnrInnIMelding(meldinger.get(i), ident);
                oppdaterBolk(brukteIdenterIDenneBolken, Arrays.asList(ident));
            }
        }
    }

    private Map<String, String> getIdentWithStatus(List<String> levendeIdenterINorge, String aarsakskode, Predicate<Map<String, String>> predicate) {
        Map<String, String> statusQuoFraAarsakskodeIdent = new HashMap<>();
        String randomIdent;
        do {
            if (levendeIdenterINorge.size() <= 0) {
                randomIdent = null;
                break;
            }
            int randomIndex = rand.nextInt(levendeIdenterINorge.size());
            randomIdent = levendeIdenterINorge.remove(randomIndex); // pass på remove
            statusQuoFraAarsakskodeIdent = getStatusQuoPaaIdent(aarsakskode, randomIdent);
        }
        while (predicate.test(statusQuoFraAarsakskodeIdent));
        statusQuoFraAarsakskodeIdent.put(IDENT, randomIdent);
        return statusQuoFraAarsakskodeIdent;
    }

    private Map<String, String> getStatusQuoPaaIdent(String aarsakskode, String fnr) {
        Map<String, String> statusQuoFraAarsakskode = new HashMap<>();

        try {
            statusQuoFraAarsakskode.putAll(aarsakskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(
                    findEnumOfAarsakskode(aarsakskode), fnr));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return statusQuoFraAarsakskode;
    }

    private AarsakskoderTrans1 findEnumOfAarsakskode(String aarsakskode) {
        for (AarsakskoderTrans1 aarsakskoderTrans1 : AarsakskoderTrans1.values()) {
            if (aarsakskoderTrans1.getAarsakskode().equals(aarsakskode)) {
                return aarsakskoderTrans1;
            }
        }
        return null;
    }

    private void putFnrInnIMelding(RsMeldingstype melding, String fnr) {
        ((RsMeldingstype1Felter) melding).setFodselsdato(fnr.substring(0, 6));
        ((RsMeldingstype1Felter) melding).setPersonnummer(fnr.substring(6));
    }

    private RsMeldingstype putIdentInnINyMelding(List<RsMeldingstype> meldinger, String fnr) {
        RsMeldingstype rsMeldingstypeIdent = new RsMeldingstype1Felter();
        putFnrInnIMelding(rsMeldingstypeIdent, fnr);
        meldinger.add(rsMeldingstypeIdent);
        return rsMeldingstypeIdent;
    }

    private void oppdaterBolk(List<String> brukteIdenterIDenneBolken, List<String> identer) {
        for (String ident : identer) {
            brukteIdenterIDenneBolken.add(ident);
        }
    }
}
