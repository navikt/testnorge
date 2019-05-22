package no.nav.registre.skd.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import no.nav.registre.skd.consumer.HodejegerenConsumer;

@Slf4j
@Service
public class HodejegerDatabaseService {

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    public void sendIdenterMedSkdMeldingerTilHodejegeren(List<String> identerMedSkdMeldinger, String miljoe) {
        identerMedSkdMeldinger.removeAll(hodejegerenConsumer.sendIdenterMedSkdMeldingerTilHodejegeren(identerMedSkdMeldinger, miljoe));
        if (!identerMedSkdMeldinger.isEmpty()) {
            log.warn("Følgende identer ble ikke oppdatert i Hodejeger-databasen: {}", identerMedSkdMeldinger.toString());
        }
    }
}
