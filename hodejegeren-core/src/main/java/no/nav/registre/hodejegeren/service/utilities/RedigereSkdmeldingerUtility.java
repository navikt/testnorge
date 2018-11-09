package no.nav.registre.hodejegeren.service.utilities;

import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype1Felter;

public class RedigereSkdmeldingerUtility {

    public static void putFnrInnIMelding(RsMeldingstype1Felter melding, String fnr) {
        melding.setFodselsdato(fnr.substring(0, 6));
        melding.setPersonnummer(fnr.substring(6));
    }

    public static void putEktefellePartnerFnrInnIMelding(RsMeldingstype1Felter melding, String identPartner) {
        melding.setEktefellePartnerFdato(identPartner.substring(0, 6));
        melding.setEktefellePartnerPnr(identPartner.substring(6));
    }

    public static RsMeldingstype1Felter opprettKopiAvSkdMelding(RsMeldingstype1Felter originalMelding, String fnr) {
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
}
