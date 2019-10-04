package no.nav.registre.arena.domain.aap;

import no.nav.registre.arena.domain.aap.andreokonomytelser.AndreOkonomYtelserV1;
import no.nav.registre.arena.domain.aap.andreokonomytelser.AnnenOkonomYtelseV1;
import no.nav.registre.arena.domain.aap.andreokonomytelser.OkonomKoder;
import no.nav.registre.arena.domain.aap.gensaksopplysninger.GensakKoder;
import no.nav.registre.arena.domain.aap.gensaksopplysninger.GensakOvKoder;
import no.nav.registre.arena.domain.aap.gensaksopplysninger.Saksopplysning;
import no.nav.registre.arena.domain.aap.institusjonsopphold.InstKoder;
import no.nav.registre.arena.domain.aap.institusjonsopphold.InstOvKoder;
import no.nav.registre.arena.domain.aap.institusjonsopphold.Institusjonsopphold;
import no.nav.registre.arena.domain.aap.periode.Periode;
import no.nav.registre.arena.domain.aap.vilkaar.Vilkaar;
import no.nav.registre.arena.domain.utils.DateString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AapFactory {

    private static boolean validString(String s) {
            return !Objects.isNull(s) && !s.trim().isEmpty();
    }

    private static void addOkonomiskYtelse(OkonomKoder kode, String verdi, List<AnnenOkonomYtelseV1> liste) {
        if (validString(verdi)) {
            liste.add(new AnnenOkonomYtelseV1(kode, verdi));
        }
    }

    private static void addGenerelleSaksopplysninger(GensakKoder kode, GensakOvKoder overordnet, String verdi, List<Saksopplysning> liste) {
        if (validString(verdi)) {
            liste.add(new Saksopplysning(kode, overordnet, verdi));
        }
    }

    private static void addInstitusjonsopphold(InstKoder kode, InstOvKoder overordnet, String verdi, List<Institusjonsopphold> liste) {
        if (validString(verdi)) {
            liste.add(new Institusjonsopphold(kode, overordnet, verdi));
        }
    }

    private static void addVilkaar(String kode, String status, List<Vilkaar> liste) {
        if (validString(status)) {
            liste.add(new Vilkaar(kode, status));
        }
    }

    public static Aap byggAAP(AAPMelding syntMelding) {

        List<AnnenOkonomYtelseV1> okonomYtelse = new ArrayList<>();
        List<Saksopplysning> saksopplysninger = new ArrayList<>();
        List<Institusjonsopphold> institusjonsopphold = new ArrayList<>();
        List<Vilkaar> vilkaar = new ArrayList<>();

        // OKONOMISKE YTELSER
        addOkonomiskYtelse(OkonomKoder.TYPE, syntMelding.getTYPE(), okonomYtelse);
        addOkonomiskYtelse(OkonomKoder.FDATO, syntMelding.getFDATO(), okonomYtelse);
        addOkonomiskYtelse(OkonomKoder.TDATO, syntMelding.getTDATO(), okonomYtelse);
        addOkonomiskYtelse(OkonomKoder.GRAD, syntMelding.getGRAD(), okonomYtelse);
        addOkonomiskYtelse(OkonomKoder.BELOP, syntMelding.getBELOP(), okonomYtelse);
        addOkonomiskYtelse(OkonomKoder.BELPR, syntMelding.getBELPR(), okonomYtelse);
        AndreOkonomYtelserV1 okonomYtelserLister = new AndreOkonomYtelserV1(okonomYtelse);

        // SAKSOPPLYSNINGER
        addGenerelleSaksopplysninger(GensakKoder.KDATO, null, syntMelding.getKDATO(), saksopplysninger);
        addGenerelleSaksopplysninger(GensakKoder.BTID, null, syntMelding.getBTID(), saksopplysninger);
        addGenerelleSaksopplysninger(GensakKoder.TUUIN, null, syntMelding.getTUUIN(), saksopplysninger);
        addGenerelleSaksopplysninger(GensakKoder.UUFOR, null, syntMelding.getUUFOR(), saksopplysninger);
        addGenerelleSaksopplysninger(GensakKoder.STUBE, null, syntMelding.getSTUBE(), saksopplysninger);
        addGenerelleSaksopplysninger(GensakKoder.OTILF, null, syntMelding.getOTILF(), saksopplysninger);
        addGenerelleSaksopplysninger(GensakKoder.OTSEK, null, syntMelding.getOTSEK(), saksopplysninger);
        addGenerelleSaksopplysninger(GensakKoder.OOPPL, null, syntMelding.getOOPPL(), saksopplysninger);
        addGenerelleSaksopplysninger(GensakKoder.BDSAT, GensakOvKoder.OOPPL, syntMelding.getBDSAT(), saksopplysninger);
        addGenerelleSaksopplysninger(GensakKoder.UFTID, GensakOvKoder.OOPPL, syntMelding.getUFTID(), saksopplysninger);
        addGenerelleSaksopplysninger(GensakKoder.BDSRP, GensakOvKoder.OOPPL, syntMelding.getBDSRP(), saksopplysninger);
        addGenerelleSaksopplysninger(GensakKoder.GRDRP, GensakOvKoder.OOPPL, syntMelding.getGRDRP(), saksopplysninger);
        addGenerelleSaksopplysninger(GensakKoder.GRDTU, GensakOvKoder.OOPPL, syntMelding.getGRDTU(), saksopplysninger);
        addGenerelleSaksopplysninger(GensakKoder.BDSTU, GensakOvKoder.OOPPL, syntMelding.getBDSTU(), saksopplysninger);
        addGenerelleSaksopplysninger(GensakKoder.GGRAD, GensakOvKoder.OOPPL, syntMelding.getGGRAD(), saksopplysninger);
        addGenerelleSaksopplysninger(GensakKoder.ORIGG, GensakOvKoder.OOPPL, syntMelding.getORIGG(), saksopplysninger);
        addGenerelleSaksopplysninger(GensakKoder.YDATO, null, syntMelding.getYDATO(), saksopplysninger);
        addGenerelleSaksopplysninger(GensakKoder.YINNT, null, syntMelding.getYINNT(), saksopplysninger);
        addGenerelleSaksopplysninger(GensakKoder.YGRAD, null, syntMelding.getYGRAD(), saksopplysninger);
        addGenerelleSaksopplysninger(GensakKoder.TLONN, null, syntMelding.getTLONN(), saksopplysninger);
        addGenerelleSaksopplysninger(GensakKoder.SPROS, GensakOvKoder.TLONN, syntMelding.getSPROS(), saksopplysninger);
        addGenerelleSaksopplysninger(GensakKoder.NORIN, GensakOvKoder.TLONN, syntMelding.getNORIN(), saksopplysninger);
        addGenerelleSaksopplysninger(GensakKoder.FAKIN, GensakOvKoder.TLONN, syntMelding.getFAKIN(), saksopplysninger);
        addGenerelleSaksopplysninger(GensakKoder.EOS, null, syntMelding.getEOS(), saksopplysninger);
        addGenerelleSaksopplysninger(GensakKoder.LAND, null, syntMelding.getLAND(), saksopplysninger);

        // INSTITUSJONSOPPHOLD
        addInstitusjonsopphold(InstKoder.STRFG, null, syntMelding.getSTRFG(), institusjonsopphold);
        addInstitusjonsopphold(InstKoder.INSTA, null, syntMelding.getINSTA(), institusjonsopphold);
        addInstitusjonsopphold(InstKoder.FRIKL, null, syntMelding.getFRIKL(), institusjonsopphold);
        addInstitusjonsopphold(InstKoder.INPER, null, syntMelding.getINPER(), institusjonsopphold);
        addInstitusjonsopphold(InstKoder.INFRA, InstOvKoder.INPER, syntMelding.getINFRA(), institusjonsopphold);
        addInstitusjonsopphold(InstKoder.INTIL, InstOvKoder.INPER, syntMelding.getINTIL(), institusjonsopphold);
        addInstitusjonsopphold(InstKoder.REDPR, null, syntMelding.getREDPR(), institusjonsopphold);
        addInstitusjonsopphold(InstKoder.INSUD, null, syntMelding.getINSUD(), institusjonsopphold);

        // VILKAAR
        addVilkaar("18-67AAR2", syntMelding.getAttensekstisyvaar2(), vilkaar);
        addVilkaar("MEDLSKAP12", syntMelding.getMEDLSKAP12(), vilkaar);
        addVilkaar("AOINORGE2", syntMelding.getAOINORGE2(), vilkaar);
        addVilkaar("INTKTBFAL2", syntMelding.getINTKTBFAL2(), vilkaar);
        addVilkaar("A11_52", syntMelding.getA11_52(), vilkaar);
        addVilkaar("YTELARBGI2", syntMelding.getYTELARBGI2(), vilkaar);
        addVilkaar("IVLGANNEN2", syntMelding.getIVLGANNEN2(), vilkaar);
        addVilkaar("AIANNEN2", syntMelding.getAIANNEN2(), vilkaar);
        addVilkaar("STRAFFGJF2", syntMelding.getSTRAFFGJF2(), vilkaar);
        addVilkaar("AAFAARBIS2", syntMelding.getAAFAARBIS2(), vilkaar);
        addVilkaar("ABIDRARAK2", syntMelding.getABIDRARAK2(), vilkaar);
        addVilkaar("AAIUBEGRF2", syntMelding.getAAIUBEGRF2(), vilkaar);
        addVilkaar("AAMEDVPLA2", syntMelding.getAAMEDVPLA2(), vilkaar);
        addVilkaar("AABEHGSTUD", syntMelding.getAABEHGSTUD(), vilkaar);
        addVilkaar("AAANNENSYK", syntMelding.getAAANNENSYK(), vilkaar);
        addVilkaar("AAFERD2", syntMelding.getAAFERD2(), vilkaar);
        addVilkaar("AAFOPTILUO", syntMelding.getAAFOPTILUO(), vilkaar);
        addVilkaar("AASAMMESYK", syntMelding.getAASAMMESYK(), vilkaar);
        addVilkaar("AASYKFORUO", syntMelding.getAASYKFORUO(), vilkaar);
        addVilkaar("AASYKTILUO", syntMelding.getAASYKTILUO(), vilkaar);
        addVilkaar("UVUT2", syntMelding.getUVUT2(), vilkaar);
        addVilkaar("AALANGUTRE", syntMelding.getAALANGUTRE(), vilkaar);
        addVilkaar("AAOPPLTILT", syntMelding.getAAOPPLTILT(), vilkaar);
        addVilkaar("AASYKDOSKA", syntMelding.getAASYKDOSKA(), vilkaar);
        addVilkaar("AASYKSKADE", syntMelding.getAASYKSKADE(), vilkaar);

        return Aap.builder()
                .andreOkonomYtelser(Collections.singletonList(okonomYtelserLister))
                .genSaksopplysninger(saksopplysninger)
                .institusjonsopphold(institusjonsopphold)
                .vilkaar(vilkaar)
                .periode(new Periode())
                .fraDato(DateString.dayMonthYearToYearMonthDay(syntMelding.getFRA_DATO()))
                .tilDato(DateString.dayMonthYearToYearMonthDay(syntMelding.getTIL_DATO()))
                .datoMottatt(DateString.dayMonthYearToYearMonthDay(syntMelding.getDATO_MOTTATT()))
                .utfall(syntMelding.getUTFALL())
                .aktivitetsfase(syntMelding.getAKTFASEKODE())
                .vedtaksvariant(syntMelding.getVEDTAKSVARIANT())
                .build();
    }
}
