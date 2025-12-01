package no.nav.testnav.libs.dto.pdlforvalter.v1;

public enum VergemaalEmbete {

            FMRO("Statsforvalteren i Rogaland"),
            FMVT("Statsforvalteren i Vestfold og Telemark"),
            FMNO("Statsforvalteren i Nordland"),
            FMTF("Statsforvalteren i Troms og Finnmark"),
            FMMR("Statsforvalteren i Møre og Romsdal"),
            FMIN("Statsforvalteren i Innlandet"),
            FMAV("Statsforvalteren i Agder"),
            FMVL("Statsforvalteren i Vestland"),
            FMOV("Statsforvalteren i Oslo og Viken"),
            FMTL("Statsforvalteren i Trøndelag");

    private final String forklaring;

    VergemaalEmbete(String forklaring) {
        this.forklaring = forklaring;
    }
}
