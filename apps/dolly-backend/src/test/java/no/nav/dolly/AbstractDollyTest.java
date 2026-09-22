package no.nav.dolly;

import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.tpsmessagingservice.MiljoerConsumer;
import no.nav.dolly.config.SecurityTestConfig;
import no.nav.dolly.config.TestDatabaseConfig;
import no.nav.dolly.config.TestOpenSearchConfig;
import no.nav.dolly.consumer.brukerservice.BrukerServiceConsumer;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.dolly.service.BrukerService;
import no.nav.dolly.service.OrganisasjonBestillingMalService;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * <p>Common base class for Dolly tests that need a full Spring application context.</p>
 * <p>Everything that takes part in the Spring test context cache key is declared here, and only here,
 * so that all subclasses share a single cached application context. The cache key includes the set of
 * {@link MockitoBean} beans and the {@link DynamicPropertySource} methods, which means that declaring
 * either of these in a subclass instead would force Spring to build an additional context for that
 * subclass.</p>
 */
@DollySpringBootTest
@Import({SecurityTestConfig.class, TestDatabaseConfig.class, TestOpenSearchConfig.class})
public abstract class AbstractDollyTest {

    @MockitoBean
    protected BrukerService brukerService;
    @MockitoBean
    protected BrukerServiceConsumer brukerServiceConsumer;
    @MockitoBean
    protected PdlDataConsumer pdlDataConsumer;
    @MockitoBean
    protected MiljoerConsumer miljoerConsumer;
    @MockitoBean
    protected OrganisasjonBestillingMalService organisasjonBestillingMalService;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://localhost:" + TestDatabaseConfig.POSTGRES.getMappedPort(5432) + "/test");
        registry.add("spring.r2dbc.username", TestDatabaseConfig.POSTGRES::getUsername);
        registry.add("spring.r2dbc.password", TestDatabaseConfig.POSTGRES::getPassword);
        registry.add("spring.flyway.enabled", () -> "false");
    }
}
