package no.nav.dolly.synt.meldekort.onnx;

enum MeldekortModelType {
    PERM,
    DAGO,
    LONN;

    static MeldekortModelType forMeldegruppe(MeldekortType meldekortType) {
        return switch (meldekortType) {
            case DAGP -> DAGO;
            case ATTF -> LONN;
            case ARBS, INDIV, FY -> PERM;
        };
    }
}

