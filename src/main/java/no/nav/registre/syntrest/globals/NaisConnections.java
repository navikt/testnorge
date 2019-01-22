package no.nav.registre.syntrest.globals;

public class NaisConnections {
    public static final String CONNECTION_MEDL = "https://nais-synthdata-medl.nais.preprod.local/api/v1/generate_medl/%o";
    public static final String ALIVE_MEDL = "https://nais-synthdata-medl.nais.preprod.local/internal/isAlive";

    public static final String CONNECTION_ARENA_MELDEKORT = "https://nais-synthdata-arena-meldekort.nais.preprod.local/api/v1/generate_meldekort/%o/%s";
    public static final String ALIVE_ARENA_MELDEKORT = "https://nais-synthdata-arena-meldekort.nais.preprod.local/internal/isAlive";

    public static final String CONNECTION_ARENA_INNTEKT = "https://nais-synthdata-arena-inntekt.nais.preprod.local/api/v1/generate";
    public static final String ALIVE_ARENA_INNTEKT = "https://nais-synthdata-arena-inntekt.nais.preprod.local/internal/isAlive";

    public static final String CONNECTION_EIA = "https://nais-synthdata-eia.nais.preprod.local/api/v1/generate";
    public static final String ALIVE_EIA = "https://nais-synthdata-eia.nais.preprod.local/internal/isAlive";

    public static final String CONNECTION_POPP = "https://nais-synthdata-popp.nais.preprod.local/api/v1/generate";
    public static final String ALIVE_POPP = "https://nais-synthdata-popp.nais.preprod.local/internal/isAlive";

    public static final String CONNECTION_TP = "https://nais-synthdata-tp.nais.preprod.local/api/v1/generate_tjenestepensjon/%o";
    public static final String ALIVE_TP = "https://nais-synthdata-tp.nais.preprod.local/internal/isAlive";


}
