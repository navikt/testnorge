package no.nav.registre.medl.provider.rs;

import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class InternalControllerTest {

    @InjectMocks
    InternalController internalController;

    @Test
    public void isReady() {
        ResponseEntity ready = internalController.isReady();
        assertSame(ready.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void isAlive() {
        ResponseEntity ready = internalController.isAlive();
        assertSame(ready.getStatusCode(), HttpStatus.OK);
    }
}