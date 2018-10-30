package no.nav.registre.hodejegeren.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype1Felter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static no.nav.registre.hodejegeren.service.AarsakskodeTilFeltnavnMapperService.*;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class EksisterendeIdenterService {

    @Autowired
    private AarsakskodeTilFeltnavnMapperService aarsakskodeTilFeltnavnMapperService;

    private Random rand;

    public EksisterendeIdenterService(Random rand) {
        this.rand = rand;
    }

    public void behandleEksisterendeIdenter(List<RsMeldingstype> meldinger, List<String> levendeIdenterINorge, List<String> singleIdenterINorge,
                                            List<String> gifteIdenterINorge, List<String> brukteIdenterIDenneBolken, String aarsakskode,
                                            String aksjonskode, String environment, Map<String, Integer> antallMeldingerPerAarsakskode) {
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
                behandleGenerellAarsak(meldinger, levendeIdenterINorge, brukteIdenterIDenneBolken, aarsakskode, aksjonskode, environment, antallMeldingerPerAarsakskode);
                break;
            case VIGSEL:
                behandleVigsel(meldinger, singleIdenterINorge, brukteIdenterIDenneBolken, aarsakskode, aksjonskode, environment, antallMeldingerPerAarsakskode);
                break;
            case SEPERASJON:
            case SKILSMISSE:
            case SIVILSTANDSENDRING:
                behandleSeperasjonSkilsmisse(meldinger, gifteIdenterINorge, brukteIdenterIDenneBolken, aarsakskode, aksjonskode, environment, antallMeldingerPerAarsakskode);
                break;
            case DOEDSMELDING:
                behandleDoedsmelding(meldinger, levendeIdenterINorge, brukteIdenterIDenneBolken, aarsakskode, aksjonskode, environment, antallMeldingerPerAarsakskode);
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
                               String aarsakskode, String aksjonskode, String environment, Map<String, Integer> antallMeldingerPerAarsakskode) {

        for (int i = 0; i < antallMeldingerPerAarsakskode.get(aarsakskode); i++) {
            if (i >= singleIdenterINorge.size() - 2) {
                // ikke nok identer i singleIdenter-liste
                break;
            }

            if (meldinger.get(i) == null) {
                // fant ikke gyldig skdmelding på index i
                continue;
            }

            Map<String, String> statusQuoFraAarsakskodeIdent1, statusQuoFraAarsakskodeIdent2;
            String randomIdent1, randomIdent2;

            do {
                if (singleIdenterINorge.size() <= 0) {
                    randomIdent1 = null;
                    break;
                }
                randomIdent1 = singleIdenterINorge.remove(rand.nextInt(singleIdenterINorge.size())); // pass på remove
                statusQuoFraAarsakskodeIdent1 = getStatusQuoPaaIdent(aarsakskode, aksjonskode, environment, randomIdent1);
            }
            while (statusQuoFraAarsakskodeIdent1.get(SIVILSTAND).equals(KoderForSivilstand.GIFT.getSivilstandKode())
                    || statusQuoFraAarsakskodeIdent1.get(SIVILSTAND).equals(KoderForSivilstand.SEPARERT.getSivilstandKode()));

            do {
                if (singleIdenterINorge.size() <= 0) {
                    randomIdent2 = null;
                    break;
                }
                randomIdent2 = singleIdenterINorge.remove(rand.nextInt(singleIdenterINorge.size())); // pass på remove
                statusQuoFraAarsakskodeIdent2 = getStatusQuoPaaIdent(aarsakskode, aksjonskode, environment, randomIdent2);
            }
            while (statusQuoFraAarsakskodeIdent2.get(SIVILSTAND).equals(KoderForSivilstand.GIFT.getSivilstandKode())
                    || statusQuoFraAarsakskodeIdent2.get(SIVILSTAND).equals(KoderForSivilstand.SEPARERT.getSivilstandKode()));

            if (randomIdent1 != null && randomIdent2 != null) {
                putFnrInnIMelding(meldinger.get(i), randomIdent1);
                ((RsMeldingstype1Felter) meldinger.get(i)).setEktefellePartnerFdato(randomIdent2.substring(0, 6));
                ((RsMeldingstype1Felter) meldinger.get(i)).setEktefellePartnerPnr(randomIdent2.substring(6));

                RsMeldingstype melding = putIdentInnINyMelding(meldinger, randomIdent2);
                ((RsMeldingstype1Felter) melding).setEktefellePartnerFdato(randomIdent1.substring(0, 6));
                ((RsMeldingstype1Felter) melding).setEktefellePartnerPnr(randomIdent1.substring(6));

                oppdaterBolk(brukteIdenterIDenneBolken, Arrays.asList(randomIdent1, randomIdent2));
            }
        }
    }

    public void behandleSeperasjonSkilsmisse(List<RsMeldingstype> meldinger, List<String> gifteIdenterINorge, List<String> brukteIdenterIDenneBolken,
                                             String aarsakskode, String aksjonskode, String environment, Map<String, Integer> antallMeldingerPerAarsakskode) {
        for (int i = 0; i < antallMeldingerPerAarsakskode.get(aarsakskode); i++) {
            if (meldinger.get(i) == null) {
                // fant ikke gyldig skdmelding på index i
                continue;
            }

            Map<String, String> statusQuoFraAarsakskodeIdent = null, statusQuoFraAarsakskodeIdentPartner;
            String randomIdent, randomIdentPartner;

            do {
                if (gifteIdenterINorge.size() <= 0) {
                    randomIdent = null;
                    break;
                }
                randomIdent = gifteIdenterINorge.remove(rand.nextInt(gifteIdenterINorge.size())); // pass på remove
                statusQuoFraAarsakskodeIdent = getStatusQuoPaaIdent(aarsakskode, aksjonskode, environment, randomIdent);
            }
            while (!statusQuoFraAarsakskodeIdent.get(SIVILSTAND).equals(KoderForSivilstand.GIFT.getSivilstandKode()));

            if (randomIdent != null && statusQuoFraAarsakskodeIdent != null) {
                randomIdentPartner = statusQuoFraAarsakskodeIdent.get(FNR_RELASJON);

                statusQuoFraAarsakskodeIdentPartner = getStatusQuoPaaIdent(aarsakskode, aksjonskode, environment, randomIdentPartner);

                if (statusQuoFraAarsakskodeIdentPartner.get(SIVILSTAND).equals(statusQuoFraAarsakskodeIdent.get(SIVILSTAND))) {
                    if (statusQuoFraAarsakskodeIdentPartner.get(FNR_RELASJON).equals(randomIdent)) {
                        putFnrInnIMelding(meldinger.get(i), randomIdent);

                        putIdentInnINyMelding(meldinger, randomIdentPartner);

                        oppdaterBolk(brukteIdenterIDenneBolken, Arrays.asList(randomIdent, randomIdentPartner));
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
                                     String aarsakskode, String aksjonskode, String environment, Map<String, Integer> antallMeldingerPerAarsakskode) {
        for (int i = 0; i < antallMeldingerPerAarsakskode.get(aarsakskode); i++) {

            if (meldinger.get(i) == null) {
                // fant ikke gyldig skdmelding på index i
                continue;
            }

            Map<String, String> statusQuoFraAarsakskodeIdent = null, statusQuoFraAarsakskodeIdentPartner;
            String randomIdent, randomIdentPartner;

            do {
                if (levendeIdenterINorge.size() <= 0) {
                    randomIdent = null;
                    break;
                }
                int randomIndex = rand.nextInt(levendeIdenterINorge.size());
                randomIdent = levendeIdenterINorge.remove(randomIndex);
                statusQuoFraAarsakskodeIdent = getStatusQuoPaaIdent(aarsakskode, aksjonskode, environment, randomIdent);
            }
            while (!statusQuoFraAarsakskodeIdent.get(DATO_DO).isEmpty() || (!statusQuoFraAarsakskodeIdent.get(STATSBORGER).equals("NORGE")));

            if(statusQuoFraAarsakskodeIdent != null){
                if (statusQuoFraAarsakskodeIdent.get(SIVILSTAND).equals(KoderForSivilstand.GIFT.getSivilstandKode())) {
                    randomIdentPartner = statusQuoFraAarsakskodeIdent.get(FNR_RELASJON);

                    statusQuoFraAarsakskodeIdentPartner = getStatusQuoPaaIdent(aarsakskode, aksjonskode, environment, randomIdentPartner);

                    if (statusQuoFraAarsakskodeIdentPartner.get(SIVILSTAND).equals(statusQuoFraAarsakskodeIdent.get(SIVILSTAND))) {
                        if (statusQuoFraAarsakskodeIdentPartner.get(FNR_RELASJON).equals(randomIdent)) {
                            putFnrInnIMelding(meldinger.get(i), randomIdent);

                            RsMeldingstype melding = new RsMeldingstype1Felter();
                            melding.setAarsakskode(AarsakskoderTrans1.SIVILSTANDSENDRING.getAarsakskode());
                            ((RsMeldingstype1Felter) melding).setFodselsdato(randomIdentPartner.substring(0, 6));
                            ((RsMeldingstype1Felter) melding).setPersonnummer(randomIdentPartner.substring(6));
                            ((RsMeldingstype1Felter) melding).setSivilstand(KoderForSivilstand.ENKE_ENKEMANN.getSivilstandKode());
                            meldinger.add(melding);

                            oppdaterBolk(brukteIdenterIDenneBolken, Arrays.asList(randomIdent, randomIdentPartner));
                        } else {
                            // personnummer i fnrRelasjon til partner matcher ikke
                        }
                    } else {
                        // ulik sivilstand på identene
                    }
                } else {
                    putFnrInnIMelding(meldinger.get(i), randomIdent);
                    oppdaterBolk(brukteIdenterIDenneBolken, Arrays.asList(randomIdent));
                }
            } else {
                // fant ikke ident
            }
        }
    }

    public void behandleGenerellAarsak(List<RsMeldingstype> meldinger, List<String> levendeIdenterINorge, List<String> brukteIdenterIDenneBolken,
                                       String aarsakskode, String aksjonskode, String environment, Map<String, Integer> antallMeldingerPerAarsakskode) {
        for (int i = 0; i < antallMeldingerPerAarsakskode.get(aarsakskode); i++) {

            if (meldinger.get(i) == null) {
                // fant ikke gyldig skdmelding på index i
                continue;
            }

            Map<String, String> statusQuoFraAarsakskodeIdent;
            String randomIdent;

            do {
                if (levendeIdenterINorge.size() <= 0) {
                    randomIdent = null;
                    break;
                }
                int randomIndex = rand.nextInt(levendeIdenterINorge.size());
                randomIdent = levendeIdenterINorge.remove(randomIndex);
                statusQuoFraAarsakskodeIdent = getStatusQuoPaaIdent(aarsakskode, aksjonskode, environment, randomIdent);
            }
            while (!statusQuoFraAarsakskodeIdent.get(DATO_DO).isEmpty() || (!statusQuoFraAarsakskodeIdent.get(STATSBORGER).equals("NORGE")));

            if (randomIdent != null) {
                putFnrInnIMelding(meldinger.get(i), randomIdent);
                oppdaterBolk(brukteIdenterIDenneBolken, Arrays.asList(randomIdent));
            }
        }
    }

    private Map<String, String> getStatusQuoPaaIdent(String aarsakskode, String aksjonskode, String environment, String fnr) {
        Map<String, String> statusQuoFraAarsakskode = new HashMap<>();

        try {
            statusQuoFraAarsakskode.putAll(aarsakskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(
                    findEnumOfAarsakskode(aarsakskode), aksjonskode, environment, fnr));
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
