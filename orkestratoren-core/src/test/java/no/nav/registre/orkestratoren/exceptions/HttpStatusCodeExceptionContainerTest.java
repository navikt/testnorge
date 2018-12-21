package no.nav.registre.orkestratoren.exceptions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class HttpStatusCodeExceptionContainerTest {

    @Test
    public void getGeneralStatusCodeClientErrorTest() {
        HttpStatusCodeExceptionContainer container = new HttpStatusCodeExceptionContainer();
        try {
            throwHttpClientErrorException(HttpStatus.BAD_REQUEST);
        } catch (HttpStatusCodeException e) {
            container.addException(e);
        }

        try {
            throwHttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (HttpStatusCodeException e) {
            container.addException(e);
        }

        assertTrue(container.getGeneralStatusCode().is4xxClientError());
    }

    @Test
    public void getGeneralStatusCodeServerErrorTest() {
        HttpStatusCodeExceptionContainer container = new HttpStatusCodeExceptionContainer();
        try {
            throwHttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (HttpStatusCodeException e) {
            container.addException(e);
        }

        assertTrue(container.getGeneralStatusCode().is5xxServerError());
    }

    @Test
    public void getGeneralStatusCodeContainerEmptyTest() {
        HttpStatusCodeExceptionContainer container = new HttpStatusCodeExceptionContainer();
        HttpStatus status = container.getGeneralStatusCode();
        assertNull(status);
    }

    @Test
    public void getResponseBodyAsStringTest() {
        String expected = "400 BAD" + System.lineSeparator() + "FEILMELDINGER" + System.lineSeparator() + "FEIL" + System.lineSeparator();

        HttpStatusCodeExceptionContainer container = new HttpStatusCodeExceptionContainer();
        container.addException(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "BAD"));
        container.addFeilmeldingBeskrivelse("FEIL");

        String s = container.getResponseBodyAsString();
        assertEquals(expected, s);
    }

    private void throwHttpServerErrorException(HttpStatus status) {
        throw new HttpServerErrorException(status);
    }

    private void throwHttpClientErrorException(HttpStatus status) {
        throw new HttpClientErrorException(status);
    }
}
