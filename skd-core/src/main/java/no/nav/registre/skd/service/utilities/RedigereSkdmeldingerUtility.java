package no.nav.registre.skd.service.utilities;

import no.nav.registre.skd.service.Endringskoder;
import no.nav.registre.skd.skdmelding.RsMeldingstype1Felter;

public class RedigereSkdmeldingerUtility {

    public static final String STATSBORGER_KODE_NORGE = "000";

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

    public static RsMeldingstype1Felter opprettStatsborgerendringsmelding(RsMeldingstype1Felter innflyttingsMelding) {
        RsMeldingstype1Felter endringAvStatsborgerskap = new RsMeldingstype1Felter().toBuilder()
                .fodselsdato(innflyttingsMelding.getFodselsdato())
                .personnummer(innflyttingsMelding.getPersonnummer())
                .statuskode("1")
                .tildelingskode("0")
                .statsborgerskap(STATSBORGER_KODE_NORGE).build();
        endringAvStatsborgerskap.setTranstype(innflyttingsMelding.getTranstype());
        endringAvStatsborgerskap.setMaskindato(innflyttingsMelding.getMaskindato());
        endringAvStatsborgerskap.setMaskintid(innflyttingsMelding.getMaskintid());
        endringAvStatsborgerskap.setAarsakskode(Endringskoder.ENDRING_STATSBORGERSKAP_BIBEHOLD.getAarsakskode());
        endringAvStatsborgerskap.setSekvensnr(innflyttingsMelding.getSekvensnr());
        return endringAvStatsborgerskap;
    }
}
