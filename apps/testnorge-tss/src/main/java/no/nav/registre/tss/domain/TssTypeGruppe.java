package no.nav.registre.tss.domain;

public enum TssTypeGruppe {
    ADVO,
    AFPO,
    APO,
    BAND,
    BB,
    BEH,
    BEHO,
    DIV,
    HPER,
    INST,
    IORG,
    KEMN,
    KOMM,
    KRED,
    KYND,
    LAB,
    LAND,
    NAV,
    OFF,
    ORG,
    RAADG,
    SHUS,
    TEPE,
    TOLK,
    UTD,
    UTL;

    public static TssTypeGruppe getGruppe(TssType type) {
        switch (type) {
            case ADVO:
                return ADVO;
            case AFPO:
                return AFPO;
            case APOP:
            case APOS:
                return APO;
            case BAND:
                return BAND;
            case BB:
                return BB;
            case AP:
            case BEH:
            case BHT:
            case FL:
            case HJS:
            case HSTA:
            case LOGO:
            case YM:
                return BEH;
            case BEUT:
            case BIRE:
                return BEHO;
            case BBE:
            case SAD:
            case X:
                return DIV;
            case AA:
            case AT:
            case AU:
            case BI:
            case R:
            case ET:
            case FA:
            case FB:
            case FO:
            case FT:
            case HE:
            case HF:
            case HP:
            case JO:
            case KE:
            case KI:
            case LE:
            case OA:
            case OI:
            case OP:
            case OR:
            case PE:
            case PF:
            case PS:
            case RA:
            case SP:
            case TH:
            case TL:
            case TP:
            case TT:
            case VE:
            case VP:
            case MT:
                return HPER;
            case INST:
                return INST;
            case FS:
                return IORG;
            case KEMN:
                return KEMN;
            case KOMM:
                return KOMM;
            case KRED:
                return KRED;
            case KYND:
            case PPT:
                return KYND;
            case LARO:
                return LAB;
            case EOS:
            case KON:
                return LAND;
            case AK:
            case AN:
            case ASEN:
            case FFU:
            case FTK:
            case HMS:
            case HTF:
            case KA:
            case KRU:
            case NK:
            case REGO:
            case RKK:
            case RTK:
            case SK:
            case TK:
                return NAV;
            case AMI:
            case ART:
            case BU:
            case IS:
            case LBS:
            case LFK:
            case LK:
            case LRB:
            case NB:
            case SR:
            case SUD:
            case TR:
                return OFF;
            case ATU:
            case BF:
            case FKL:
            case KS:
            case LM:
            case LS:
            case OM:
            case OPT:
            case PR:
            case TI:
            case TLO:
                return ORG;
            case LERA:
            case ROL:
            case RT:
                return RAADG;
            case HFO:
            case RHFO:
            case SYKH:
                return SHUS;
            case TEPE:
                return TEPE;
            case TOLK:
                return TOLK;
            case BH:
            case GVS:
            case HU:
                return UTD;
            case UTL:
                return UTL;
        }
        throw new RuntimeException("Ukjent samhandler gruppe: " + type);
    }

    public static boolean skalHaOrgnummer(TssTypeGruppe gruppe) {
        switch (gruppe) {
            case AFPO:
            case BEHO:
            case KEMN:
            case OFF:
            case ORG:
                return true;
        }
        return false;
    }

    public static String identKodeType(TssTypeGruppe gruppe) {
        if (skalHaOrgnummer(gruppe)) {
            return "ORG";
        }
        return "FNR";
    }
}
