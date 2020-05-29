package no.nav.registre.orkestratoren;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LocalApplicationStarter.class, properties = "classpath:application-test.properties")
public class LocalApplicationStarterTest {

    @Test
    public void contextLoads() {
    }
}
