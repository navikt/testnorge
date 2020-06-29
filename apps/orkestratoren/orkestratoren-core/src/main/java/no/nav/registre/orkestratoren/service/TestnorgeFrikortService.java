package no.nav.registre.orkestratoren.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeFrikortConsumer;
import no.nav.registre.orkestratoren.consumer.rs.response.GenererFrikortResponse;
import no.nav.registre.orkestratoren.consumer.rs.response.GenererFrikortResponse.LeggPaaKoeStatus;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserFrikortRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestnorgeFrikortService {

    private final TestnorgeFrikortConsumer testnorgeFrikortConsumer;

    public List<GenererFrikortResponse> genererFrikortEgenmeldinger(SyntetiserFrikortRequest syntetiserFrikortRequest) {
        var genererFrikortResponse = testnorgeFrikortConsumer.startSyntetisering(syntetiserFrikortRequest);
        for (var egenandel : genererFrikortResponse) {
            if (LeggPaaKoeStatus.ERROR == egenandel.getLagtPaaKoe()) {
                log.error("Kunne ikke legge egenandel på kø. Feilende xml: {}", egenandel.getXml());
            }
        }
        return genererFrikortResponse;
    }
}
