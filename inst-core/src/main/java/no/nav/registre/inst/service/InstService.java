package no.nav.registre.inst.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inst.consumer.rs.InstSyntetisererenConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class InstService {

    @Autowired
    InstSyntetisererenConsumer instSyntRestConsumer;

    public List<Map<String, String>> finnSyntetiserteMeldinger(int numToGenerate) {
        return instSyntRestConsumer.hentInstMeldingerFromSyntRest(numToGenerate);
    }
}
