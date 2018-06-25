package no.nav.regression;

import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TeamRepository;
import no.nav.dolly.repository.TestGruppeRepository;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public abstract class InMememoryDbTestSetup {

    @Autowired
    public BrukerRepository brukerRepository;

    @Autowired
    public TestGruppeRepository testGruppeRepository;

    @Autowired
    public TeamRepository teamRepository;

    @Autowired
    public IdentRepository identRepository;

    @Before
    public void clearDatabase() {
    }
}
