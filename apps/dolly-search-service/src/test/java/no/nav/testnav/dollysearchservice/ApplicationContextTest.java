package no.nav.testnav.dollysearchservice;

import no.nav.dolly.libs.test.DollyApplicationContextTest;
import no.nav.dolly.libs.test.DollySpringBootTest;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DollySpringBootTest
class ApplicationContextTest extends DollyApplicationContextTest {

    @MockitoBean
    @SuppressWarnings("unused")
    OpenSearchClient openSearchClient; // Not actually used in any tests, but required by BestillingQueryService.

}