package no.nav.dolly.synt.meldekort.generator;

import lombok.experimental.UtilityClass;

import static java.util.Objects.isNull;

@UtilityClass
class MeldekortXmlMapper {

    private static final String SOAP_ENV_OPEN = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><soapenv:Body><MottakMeldekort xmlns=\"\"><Meldekort xmlns:ns2=\"http://www.aetat.no/arena/mk_meldekort.xsd\" Id=\"%d\">";
    private static final String SOAP_ENV_CLOSE = "</Meldekort></MottakMeldekort></soapenv:Body></soapenv:Envelope>";
    private static final String KONTROLLRESULTAT = "<ns2:Kontrollresultat><ns2:Arsakskoder/><ns2:Status>OK</ns2:Status><ns2:Veiledning>false</ns2:Veiledning><ns2:Returbrev>00</ns2:Returbrev></ns2:Kontrollresultat>";

    static String toXml(int id, GeneratedMeldekort meldekort, Double arbeidstimerOverride) {
        var xmlBuilder = new StringBuilder();
        xmlBuilder.append(SOAP_ENV_OPEN.formatted(id));
        xmlBuilder.append("<ns2:Spm>");
        xmlBuilder.append(jaNeiTag("Arbeidssoker", meldekort.arbeidssoker()));
        xmlBuilder.append(jaNeiTag("Arbeidet", meldekort.arbeidet()));
        xmlBuilder.append(jaNeiTag("Syk", meldekort.syk()));
        xmlBuilder.append(jaNeiTag("AnnetFravaer", meldekort.annetFravaer()));
        xmlBuilder.append(jaNeiTag("Kurs", meldekort.kurs()));
        xmlBuilder.append("<ns2:Forskudd><ns2:Verdi>").append(PythonText.lower(meldekort.forskudd())).append("</ns2:Verdi></ns2:Forskudd>");
        xmlBuilder.append("<ns2:MeldekortDager>");
        var timerOverride = isNull(arbeidstimerOverride) ? null : PythonText.number(arbeidstimerOverride);
        for (var dag : meldekort.meldekortDager()) {
            xmlBuilder.append("<ns2:MeldekortDag>");
            xmlBuilder.append("<ns2:Dag>").append(dag.dag()).append("</ns2:Dag>");
            xmlBuilder.append("<ns2:ArbeidetTimerSum><ns2:Verdi>").append(isNull(timerOverride) ? dag.arbeidetTimerSum() : timerOverride).append("</ns2:Verdi></ns2:ArbeidetTimerSum>");
            xmlBuilder.append("<ns2:Syk><ns2:Verdi>").append(PythonText.lower(dag.syk())).append("</ns2:Verdi></ns2:Syk>");
            xmlBuilder.append("<ns2:AnnetFravaer><ns2:Verdi>").append(PythonText.lower(dag.annetFravaer())).append("</ns2:Verdi></ns2:AnnetFravaer>");
            xmlBuilder.append("<ns2:Kurs><ns2:Verdi>").append(PythonText.lower(dag.kurs())).append("</ns2:Verdi></ns2:Kurs>");
            xmlBuilder.append("<ns2:Meldegruppe>").append(dag.meldegruppe()).append("</ns2:Meldegruppe>");
            xmlBuilder.append("</ns2:MeldekortDag>");
        }
        xmlBuilder.append("</ns2:MeldekortDager>");
        xmlBuilder.append("<ns2:Signatur><ns2:Verdi>").append(meldekort.signatur()).append("</ns2:Verdi></ns2:Signatur>");
        xmlBuilder.append("</ns2:Spm>");
        xmlBuilder.append(KONTROLLRESULTAT);
        xmlBuilder.append(SOAP_ENV_CLOSE);
        return xmlBuilder.toString();
    }

    private static String jaNeiTag(String tagName, Object value) {
        return "<ns2:%s><ns2:SvarJa><ns2:Verdi>%s</ns2:Verdi></ns2:SvarJa><ns2:SvarNei><ns2:Verdi>%s</ns2:Verdi></ns2:SvarNei></ns2:%s>"
                .formatted(tagName, PythonText.lower(value), Boolean.toString(PythonText.not(value)), tagName);
    }

}
