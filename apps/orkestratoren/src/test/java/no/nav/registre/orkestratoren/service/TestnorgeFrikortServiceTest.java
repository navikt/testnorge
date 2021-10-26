package no.nav.registre.orkestratoren.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeFrikortConsumer;
import no.nav.registre.orkestratoren.consumer.rs.response.GenererFrikortResponse;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserFrikortRequest;

@ExtendWith(MockitoExtension.class)
public class TestnorgeFrikortServiceTest {

    @Mock
    private TestnorgeFrikortConsumer testnorgeFrikortConsumer;

    @InjectMocks
    private TestnorgeFrikortService testnorgeFrikortService;

    private List<GenererFrikortResponse> expectedResponse;

    @BeforeEach
    public void setUp() {
        expectedResponse = Collections.singletonList(new GenererFrikortResponse(GenererFrikortResponse.LeggPaaKoeStatus.OK, "SomeXml"));
    }

    @Test
    public void shouldGenerereSamordningsmeldinger() {
        var avspillergruppeId = 123L;
        var miljoe = "t1";
        var antallNyeIdenter = 2;
        var syntetiserFrikortRequest = new SyntetiserFrikortRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        when(testnorgeFrikortConsumer.startSyntetisering(syntetiserFrikortRequest)).thenReturn(expectedResponse);

        var response = testnorgeFrikortService.genererFrikortEgenmeldinger(syntetiserFrikortRequest);

        assertThat(response.get(0).getLagtPaaKoe(), equalTo(GenererFrikortResponse.LeggPaaKoeStatus.OK));
        verify(testnorgeFrikortConsumer).startSyntetisering(syntetiserFrikortRequest);
    }
}