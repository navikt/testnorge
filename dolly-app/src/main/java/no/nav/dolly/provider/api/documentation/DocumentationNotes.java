package no.nav.dolly.provider.api.documentation;

public final class DocumentationNotes {

    private DocumentationNotes() {
    }

    private static final String RANDOM_ADRESSE = "For å kunne styre gyldig boadresse basert på kommunenr eller postnummer og med adressesøk i backend: <br />"
            +"\"adresseNrInfo\": { <br />"
            + " &nbsp; \"nummertype\": \"KOMMUNENR/POSTNR\" <br />"
            + " &nbsp; \"nummer\": \"string\", <br />"
            + "} <br /><br />";
    private static final String ADRESSE_COMMON =
            "       &nbsp; &nbsp; \"postnr\": \"string\", <br />"
                    + "     &nbsp; &nbsp; \"kommunenr\": \"string\", <br />"
                    + "     &nbsp; &nbsp; \"flyttedato\": \"string\" <br />";
    private static final String BOADRESSE_COMMENT = "Json-parametere for boadresse har følgende parametre: <br />"
            + " For gateadresse:  <br />"
            + "     &nbsp; \"boadresse\": {<br />"
            + "     &nbsp; &nbsp; \"adressetype\": \"GATE\", <br />"
            + "     &nbsp; &nbsp; \"gateadresse\": \"string\", <br />"
            + "     &nbsp; &nbsp; \"husnummer\": \"string\", <br />"
            + "     &nbsp; &nbsp; \"gatekode\": \"string\",<br />"
            + ADRESSE_COMMON + "} <br />"
            + " For matrikkeladresse: <br />"
            + "     &nbsp;   \"boadresse\": {<br />"
            + "     &nbsp; &nbsp; \"adressetype\": \"MATR\", <br />"
            + "     &nbsp; &nbsp; \"mellomnavn\": \"string\", <br />"
            + "     &nbsp; &nbsp; \"gardsnr\": \"string\", <br />"
            + "     &nbsp; &nbsp; \"bruksnr\": \"string\", <br />"
            + "     &nbsp; &nbsp; \"festenr\": \"string\", <br />"
            + "     &nbsp; &nbsp; \"undernr\": \"string\", <br />"
            + ADRESSE_COMMON + "} <br /> <br />";

    private static final String UTEN_ARBEIDSTAKER = "I bestilling benyttes ikke feltet for arbeidstaker. <br /><br />";

    private static final String FULLT_NAVN =
            "     &nbsp; &nbsp; &nbsp; \"etternavn\": \"string\", <br />"
                    + "     &nbsp; &nbsp; &nbsp; \"fornavn\": \"string\", <br />"
                    + "     &nbsp; &nbsp; &nbsp; \"mellomnavn\": \"string\" <br />";

    private static final String ADRESSAT = "&nbsp;   \"adressat\": {<br />";

    private static final String EPILOG = "     &nbsp; } </br /></br />";
    private static final String EPILOG_2 = "     &nbsp; &nbsp; }, </br />";
    private static final String FALSK_IDENTITET_TYPE = "     &nbsp; \"falskIdentitet\": {<br />";

    private static final String KONTAKTINFORMASJON_DOEDSBO = "For kontakinformasjon for dødsbo kan feltet <b>adressat</b> ha en av fire objekttyper: <br />"
            + "For organisasjon eller advokat:<br />"
            + ADRESSAT
            + "     &nbsp; &nbsp; \"adressatType\": \"ORGANISASJON/ADVOKAT\", <br />"
            + "     &nbsp; &nbsp; \"kontaktperson\": { <br />"
            + FULLT_NAVN
            + EPILOG_2
            + "     &nbsp; &nbsp; \"organisajonsnavn\": \"string\", <br />"
            + "     &nbsp; &nbsp; \"organisajonsnummer\": \"string\" <br />"
            + EPILOG
            + "For kontaktperson med ID:<br />"
            + ADRESSAT
            + "     &nbsp; &nbsp; \"adressatType\": \"PERSON_MEDID\", <br />"
            + "     &nbsp; &nbsp; \"idnummer\": \"string\"<br />"
            + EPILOG
            + "For kontaktperson uten ID:<br />"
            + ADRESSAT
            + "     &nbsp; &nbsp; \"adressatType\": \"PERSON_UTENID\", <br />"
            + "     &nbsp; &nbsp; \"navn\": { <br />"
            + FULLT_NAVN
            + EPILOG_2
            + "     &nbsp; &nbsp; \"foedselsdato\": \"string\" <br />"
            + EPILOG;

    private static final String FALSK_IDENTITET = "Falsk identitet inneholder et abstrakt felt, <b>rettIdentitet</b>, som har en av tre objekttyper: <br />"
            + "For identitet ukjent:<br />"
            + FALSK_IDENTITET_TYPE
            + "     &nbsp; &nbsp; \"identitetType\": \"UKJENT\", <br />"
            + "     &nbsp; &nbsp; \"rettIdentitetErUkjent\": true <br />"
            + EPILOG
            + "For identitet med personnummer:<br />"
            + FALSK_IDENTITET_TYPE
            + "     &nbsp; &nbsp; \"identitetType\": \"ENTYDIG\", <br />"
            + "     &nbsp; &nbsp; \"rettIdentitetVedIdentifikasjonsnummer\": \"&lt;fnr/dnr&gt;\" <br />"
            + EPILOG
            + "For identitet med opplysninger:<br />"
            + FALSK_IDENTITET_TYPE
            + "     &nbsp; &nbsp; \"identitetType\": \"OMTRENTLIG\", <br />"
            + "     &nbsp; &nbsp; \"foedselsdato\": \"&lt;dato&gt;\" <br />"
            + "     &nbsp; &nbsp; \"kjoenn\": \"MANN/KVINNE/UBESTEMT\" <br />"
            + "     &nbsp; &nbsp; \"personnavn\":{<br />"
            + FULLT_NAVN
            + EPILOG_2
            + "     &nbsp; &nbsp; \"statsborgerskap\": \"[AUS,GER,FRA,etc]\" <br />"
            + EPILOG;

    public static final String AAREG_JSON_COMMENT = "For AAREG-integrasjon er følgende felter kodeverksfelter med kodeverksdomene i parentes: <br />"
            + "&nbsp;&nbsp;&nbsp; arbeidsforholdstype: (Arbeidsforholdstyper) <br />"
            + "&nbsp;&nbsp;&nbsp; arbeidstidsordning: (Arbeidstidsordninger) <br />"
            + "&nbsp;&nbsp;&nbsp; avlønningstype: (Avlønningstyper) <br />"
            + "&nbsp;&nbsp;&nbsp; identtype: (Personidenter) <br />"
            + "&nbsp;&nbsp;&nbsp; land: (LandkoderISO2) <br />"
            + "&nbsp;&nbsp;&nbsp; permisjonOgPermittering: (PermisjonsOgPermitteringsBeskrivelse) <br />"
            + "&nbsp;&nbsp;&nbsp; yrke: (Yrker) <br /><br />"
            + "Alle datofelter har forventet innhold \"yyyy-MM-ddT00:00:00\". <br /> <br />"
            + "Attributt aktoer er abstrakt og kan ha et av følgende objekttyper: <br />"
            + "For organisajon: <br />"
            + "&nbsp; \"aktoer\": { <br />"
            + "&nbsp;&nbsp;&nbsp; \"aktoertype\": \"ORG\", <br />"
            + "&nbsp;&nbsp;&nbsp; \"orgnummer\": \"<orgnummer>\" <br />"
            + "&nbsp; } <br />"
            + "For person: <br />"
            + "&nbsp; \"aktoer\": { <br />"
            + "&nbsp;&nbsp;&nbsp; \"aktoertype\": \"PERS\", <br />"
            + "&nbsp;&nbsp;&nbsp; \"ident\": \"<ident>\" <br />"
            + "&nbsp; } <br /><br />";

    public static final String BESTILLING_BESKRIVELSE = RANDOM_ADRESSE + BOADRESSE_COMMENT + AAREG_JSON_COMMENT + UTEN_ARBEIDSTAKER + KONTAKTINFORMASJON_DOEDSBO + FALSK_IDENTITET;

}
