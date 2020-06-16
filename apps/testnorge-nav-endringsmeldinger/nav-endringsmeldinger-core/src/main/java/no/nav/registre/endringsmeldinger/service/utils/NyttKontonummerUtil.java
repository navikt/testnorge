package no.nav.registre.endringsmeldinger.service.utils;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.time.LocalDate;

public class NyttKontonummerUtil {

    private NyttKontonummerUtil() {

    }

    private static final String KILDE = "TPSF";
    private static final String BRUKER_ID = "ORK";

    public static Document opprettDokumentForNyttKontonummer(
            String ident,
            String kontonummer
    ) throws ParserConfigurationException {
        var factory = DocumentBuilderFactory.newInstance();
        var builder = factory.newDocumentBuilder();

        var document = builder.newDocument();

        var personData = document.createElement("sfePersonData");
        document.appendChild(personData);

        var ajourforing = document.createElement("sfeAjourforing");
        personData.appendChild(ajourforing);

        var systemInfo = document.createElement("systemInfo");
        ajourforing.appendChild(systemInfo);

        var kilde = document.createElement("kilde");
        kilde.appendChild(document.createTextNode(KILDE));
        systemInfo.appendChild(kilde);

        var brukerId = document.createElement("brukerID");
        brukerId.appendChild(document.createTextNode(BRUKER_ID));
        systemInfo.appendChild(brukerId);

        var endreGironrNorsk = document.createElement("endreGironrNorsk");
        ajourforing.appendChild(endreGironrNorsk);

        var offentligIdent = document.createElement("offentligIdent");
        offentligIdent.appendChild(document.createTextNode(ident));
        endreGironrNorsk.appendChild(offentligIdent);

        var giroNrNorsk = document.createElement("giroNrNorsk");
        giroNrNorsk.appendChild(document.createTextNode(kontonummer));
        endreGironrNorsk.appendChild(giroNrNorsk);

        var datoGiroNrNorsk = document.createElement("datoGiroNrNorsk");
        var dagensDato = LocalDate.now();
        datoGiroNrNorsk.appendChild(document.createTextNode(dagensDato.toString()));
        endreGironrNorsk.appendChild(datoGiroNrNorsk);

        return document;
    }
}
