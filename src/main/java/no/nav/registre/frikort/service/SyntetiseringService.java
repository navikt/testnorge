package no.nav.registre.frikort.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.frikort.consumer.rs.FrikortSyntetisererenConsumer;
import no.nav.registre.frikort.consumer.rs.response.SyntFrikortResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SyntetiseringService {

    @Autowired
    private FrikortSyntetisererenConsumer frikortSyntetisererenConsumer;

    public Map<String, List<SyntFrikortResponse>> hentSyntetiskeFrikort(Map<String, Integer> request) {

        return frikortSyntetisererenConsumer.hentFrikortFromSyntRest(request);
    }

}
