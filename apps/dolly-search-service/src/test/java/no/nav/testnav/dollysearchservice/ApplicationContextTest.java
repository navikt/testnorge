package no.nav.testnav.dollysearchservice;

import no.nav.dolly.libs.test.DollyApplicationContextTest;
import no.nav.dolly.libs.test.DollySpringBootTest;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DollySpringBootTest
class ApplicationContextTest extends DollyApplicationContextTest {

    @MockitoBean
    @SuppressWarnings("unused")
    RestHighLevelClient restHighLevelClient; // Not actually used in any tests, but required by BestillingQueryService.

}