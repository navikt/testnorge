package no.nav.brregstub.api;

import java.util.LinkedHashMap;
import java.util.Map;

public class UnderstatusKode {

    public static final Map<Integer, String> understatusKoder = new LinkedHashMap<>() {
        {
            put(0, "Data returnert");
            put(1, "Enhet x er slettet som dublett - korrekt enhet y er innført");
            put(2, "Enhet x er slettet som sammenslått - korrekt enhet y er innført");
            put(3, "Enhet x er registreringsenheten til den enheten som ble bestilt!");
            put(10, "Orgnr i input ikke gitt");
            put(100, "Enhet x aldri opprettet");
            put(110, "Enhet x er slettet");
            put(120, "Enhet x er slettet som dublett eller sammenslått - korrekt enhet y er slettet");
            put(130, "Enhet x er en bedrift/virksomhet, juridisk enhet er y");
            put(160, "Input er ikke et organisasjonsnummer eller et fødselsnr");
            put(170, "x er ikke et gyldig organisasjonsnummer");
            put(180, "Personen x finnes ikke i vår database");
            put(181, "Personen x er registrert død");
            put(182, "Personen x er registrert ugyldig");
            put(183, "Personen x har verge");
            put(190, "Enhet x er ikke en bedrift/virksomhet");
            put(200, "Enhet x det spørres på er en registreringsenhet");
            put(210, "Enhet x er ikke registrert i Frivillighetsregisteret");
            put(2000, "Ingen endringer finnes i siste periode ,siste døgn/helg,");
            put(1010, "Enhet x har ikke forretningsadresse");
            put(1020, "Enhet x har ikke postadresse");
            put(1030, "Enhet x har ikke adresse");
            put(1060, "Enhet x har ikke stiftelsesdato");
            put(1070, "Enhet x har ikke målform");
            put(1080, "Enhet x har ikke reklame");
            put(1090, "Enhet x har ikke virksomhet/art/bransje");
            put(1100, "Enhet x har ikke formål");
            put(1111, "Enhet x har ikke vedtektsdato");
            put(1115, "Enhet x har ikke telefon");
            put(1116, "Enhet x har ikke telefax");
            put(1117, "Enhet x har ikke mobiltelefon");
            put(1118, "Enhet x har ikke e-postadresse");
            put(1119, "Enhet x har ikke hjemmeside");
            put(1120, "Enhet x har ikke særlige opplysninger");
            put(1125, "Enhet x har ikke næringskode");
            put(1130, "Enhet x har ikke sektorkode");
            put(1135, "Enhet x har ikke ansatte");
            put(1140, "Enhet x har ikke kapital");
            put(1145, "Enhet x har ikke bedrifter/virksomheter");
            put(1150, "Enhet x inngår ikke i konsern");
            put(1155, "Enhet x har ikke dato for oppstart");
            put(1160, "Enhet x har ikke dato for eierskifte");
            put(1165, "Enhet x har ikke hovedforetak");
            put(1180, "Enhet x har ikke rolleblokk n ,et sett av roller");
            put(1190, "Enhet x har ikke frivillighetsregisterdata");
        }
    };
}
