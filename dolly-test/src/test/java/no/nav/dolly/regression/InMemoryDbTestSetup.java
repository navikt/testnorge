package no.nav.dolly.regression;

import no.nav.dolly.regression.testrepositories.BestillingProgressTestRepository;
import no.nav.dolly.regression.testrepositories.BestillingTestRepository;
import no.nav.dolly.regression.testrepositories.BrukerTestRepository;
import no.nav.dolly.regression.testrepositories.GruppeTestRepository;
import no.nav.dolly.regression.testrepositories.IdentTestRepository;
import no.nav.dolly.regression.testrepositories.TeamTestRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
@ActiveProfiles(value = "test")
public abstract class InMemoryDbTestSetup {

    @Autowired
    public TeamTestRepository teamTestRepository;

    @Autowired
    public BrukerTestRepository brukerTestRepository;

    @Autowired
    public GruppeTestRepository gruppeTestRepository;

    @Autowired
    public IdentTestRepository identTestRepository;

    @Autowired
    public BestillingTestRepository bestillingTestRepository;

    @Autowired
    public BestillingProgressTestRepository bestillingProgressTestRepository;
}
