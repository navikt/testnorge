package no.nav.testnav.apps.adresseservice;

import no.nav.dolly.libs.test.DollyApplicationContextTest;
import no.nav.dolly.libs.test.DollySpringBootTest;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DollySpringBootTest
class ApplicationContextTest extends DollyApplicationContextTest {

    @MockitoBean
    private OpenSearchClient openSearchClient;
}