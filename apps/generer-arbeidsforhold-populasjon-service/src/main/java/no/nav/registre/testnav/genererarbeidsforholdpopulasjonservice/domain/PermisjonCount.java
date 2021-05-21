package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding.Permisjon;

@Slf4j
@Getter
public class PermisjonCount {
    float velferdspermisjon = 0;
    float utdanningspermisjon = 0;
    float permisjonMedForeldrepenger = 0;
    float permisjonVedMilitaertjeneste = 0;
    float permisjon = 0;
    float permittering = 0;

    public PermisjonCount(List<Permisjon> permisjoner) {
        if (permisjoner == null) {
            return;
        }
        for (var permisjon : permisjoner) {
            switch (permisjon.getBeskrivelse()) {
                case "velferdspermisjon":
                    velferdspermisjon++;
                    break;
                case "utdanningspermisjon":
                    utdanningspermisjon++;
                    break;
                case "permisjonMedForeldrepenger":
                    permisjonMedForeldrepenger++;
                    break;
                case "permisjonVedMilitaertjeneste":
                    permisjonVedMilitaertjeneste++;
                    break;
                case "permisjon":
                    this.permisjon++;
                    break;
                case "permittering":
                    permittering++;
                    break;
                default:
                    log.warn("Ukjent permisjons beskrivelse {}", permisjon.getBeskrivelse());
                    break;
            }
        }
    }

}
