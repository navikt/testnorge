package no.nav.registre.spion.utils;

public class SwaggerUtils {

    private SwaggerUtils() {
    }

    public static final String ANTALL_PERIODER_DESCRIPTION =  "- **antallPerioder**: antall perioder det skal " +
            "syntetiseres vedtak for. Hvis satt gjelder det for alle personer/identer. Default: for hver person det " +
            "skal syntetiseres vedtak for blir et tilfeldig tall mellom 1 og 15 valgt.\n\n ";
    public static final String ANTALL_PERSONER_DESCRIPTION = "- **antallPersoner**: antall personer det skal " +
            "syntetiseres vedtak for. Tilfeldig ident med arbeidsforhold blir hentet fra aareg for hver person. " +
            "Default: 1. \n\n ";
    public static final String AVPILLERGRUPPE_ID_DESCRUPTION = "- **avspillergruppeId**: hvilken avspillergruppe i " +
            "tps forvalteren som identer skal hentes fra. Default: avspillergruppeId for Mini-Norge(q2). \n\n ";
    public static final String MILJOE_DESCRIPTION = "- **miljoe**: hvilket miljø som avspillergruppeId er koblet " +
            "opp mot. Default: q2. \n\n ";
    public static final String START_DATO_DESCRIPTION = "- **startDato**: startdato for perioden det ønsket å " +
            "syntetisere vedtak for. Default: hvis sluttDato har verdi blir den satt til sluttDato minus 18 måneder." +
            " Hvis sluttDato også mangler verdi blir den satt til dagens dato minus 18 måneder. \n\n";
    public static final String SLUTT_DATO_DESCRIPTION = "- **sluttDato**: sluttdato for perioden det ønskes å " +
            "syntetisere vedtak for. Default: hvis startDato har verdi blir den satt til startDato pluss 18 måneder." +
            " Hvis startDato også mangler verdi blir den satt til dagens dato. \n\n";

    public static final String REQUEST_DESCRIPTION = "Ingen av parameterne i request body er nødvendig. Hvis en " +
            "parameter mangler blir den satt til default verdi. \n\n" + ANTALL_PERIODER_DESCRIPTION +
            ANTALL_PERSONER_DESCRIPTION + AVPILLERGRUPPE_ID_DESCRUPTION + MILJOE_DESCRIPTION +START_DATO_DESCRIPTION
            + SLUTT_DATO_DESCRIPTION;
}
