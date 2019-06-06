package no.nav.dolly.regression;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.dolly.regression.scenarios.testrepositories.BrukerTestRepository;
import no.nav.dolly.regression.scenarios.testrepositories.GruppeTestRepository;
import no.nav.dolly.regression.scenarios.testrepositories.TeamTestRepository;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.GruppeRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TeamRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
@ActiveProfiles(value = "test")
public abstract class InMemoryDbTestSetup {

    @Autowired
    public BrukerRepository brukerRepository;

    @Autowired
    public GruppeRepository gruppeRepository;

    @Autowired
    public TeamRepository teamRepository;

    @Autowired
    public IdentRepository identRepository;

    @Autowired
    public BestillingRepository bestillingRepository;

    @Autowired
    public BestillingProgressRepository bestillingProgressRepository;

    @Autowired
    public TeamTestRepository teamTestRepository;

    @Autowired
    public BrukerTestRepository brukerTestRepository;

    @Autowired
    public GruppeTestRepository gruppeTestRepository;
}
