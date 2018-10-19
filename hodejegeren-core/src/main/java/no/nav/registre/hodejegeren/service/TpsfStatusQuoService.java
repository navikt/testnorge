package no.nav.registre.hodejegeren.service;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TpsfStatusQuoService {
    
    public List<String> finnEksisterendeIdenterBasertPaaMeldingensAarsakskode(final String aarsakskode, int antall) {
        //start med switch case årsakskode, for å hente FNR basert på kriterier
        switch (aarsakskode) {
        case "39":
        case "07":
        case "10":
        case "24":
        case "26":
        case "56":
        case "35":
        case "44":
        case "47":
        case "51":
        case "81":
        case "98":
        case "38":
        case "28":
        case "29":
        case "48":
        case "49":
        case "43":
        case "32":
            //levende
            break;
        
        case "34":
            //GIFT eller levende (statistikk?
            break;
        case "40":
            //            barn/fødselsmelding
            break;
        case "41": //Endring av oppholdstillatelse
            //innvandret
            break;
        case "25":
            //Annulere flytting
            break;
        case "85":    //SIVILSTANDSENDRING . Melding om enke/-mann/gjenlevende partner
            //        NY_DØDSMELDING eller GIFT
            break;
        case "11":
            //Singel
            break;
        case "14":
        case "18":
            //GIFT
            break;
        case "06"://Navneendring - første gangs navnevalg"
            //LEVENDE eller FØRSTEGANGS_NAVNEENDRING
            break;
        }
        return null;
    }
    
    //finn status på
}
