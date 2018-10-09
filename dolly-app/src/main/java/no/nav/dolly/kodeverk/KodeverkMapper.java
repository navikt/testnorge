package no.nav.dolly.kodeverk;

import no.nav.dolly.domain.resultset.kodeverk.KodeAdjusted;
import no.nav.dolly.domain.resultset.kodeverk.KodeverkAdjusted;
import no.nav.tjenester.kodeverk.api.v1.Betydning;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

import static no.nav.dolly.util.UtilFunctions.isNullOrEmpty;

@Service
public class KodeverkMapper {

    public KodeverkAdjusted mapBetydningToAdjustedKodeverk(String kodeverkNavn, Map<String, List<Betydning>> kodeMap){
        KodeverkAdjusted kodeverk = new KodeverkAdjusted();
        kodeverk.setKoder(new ArrayList<>());
        kodeverk.setName(kodeverkNavn);

        for (Map.Entry<String, List<Betydning>> entry : kodeMap.entrySet()) {

            if(isNullOrEmpty(entry.getValue())){
                continue;
            }

            Betydning betydning = entry.getValue().get(0);
            String term = betydning.getBeskrivelser().get("nb").getTerm();
            KodeAdjusted kode =  KodeAdjusted.builder()
                    .label(entry.getKey() + " - " + term)
                    .value(entry.getKey())
                    .build();

            kodeverk.getKoder().add(kode);
        }

        return kodeverk;
    }
}
