package no.nav.registre.endringsmeldinger.service.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.time.LocalDate;

public class NyttKontonummerUtil {

    private static final String KILDE = "TPSF";
    private static final String BRUKER_ID = "ORK";

    public static Document opprettDokumentForNyttKontonummer(String ident, String kontonummer) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.newDocument();

        Element personData = document.createElement("sfePersonData");
        document.appendChild(personData);

        Element ajourforing = document.createElement("sfeAjourforing");
        personData.appendChild(ajourforing);

        Element systemInfo = document.createElement("systemInfo");
        ajourforing.appendChild(systemInfo);

        Element kilde = document.createElement("kilde");
        kilde.appendChild(document.createTextNode(KILDE));
        systemInfo.appendChild(kilde);

        Element brukerId = document.createElement("brukerID");
        brukerId.appendChild(document.createTextNode(BRUKER_ID));
        systemInfo.appendChild(brukerId);

        Element endreGironrNorsk = document.createElement("endreGironrNorsk");
        ajourforing.appendChild(endreGironrNorsk);

        Element offentligIdent = document.createElement("offentligIdent");
        offentligIdent.appendChild(document.createTextNode(ident));
        endreGironrNorsk.appendChild(offentligIdent);

        Element giroNrNorsk = document.createElement("giroNrNorsk");
        giroNrNorsk.appendChild(document.createTextNode(kontonummer));
        endreGironrNorsk.appendChild(giroNrNorsk);

        Element datoGiroNrNorsk = document.createElement("datoGiroNrNorsk");
        LocalDate dagensDato = LocalDate.now();
        datoGiroNrNorsk.appendChild(document.createTextNode(String.format("%s-%s-%s", dagensDato.getYear(), dagensDato.getMonthValue(), dagensDato.getDayOfMonth())));
        endreGironrNorsk.appendChild(datoGiroNrNorsk);

        return document;
    }
}
