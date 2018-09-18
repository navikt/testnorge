package no.nav.identpool;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.identpool.ident.repository.IdentRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ComponentTestConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class ComponentTestbase {
    protected static final String IDENT_V1_BASEURL = "/identifikator/v1";
    protected static final String OPERASJON_HENT = "/hent";
    protected static final String OPERASJON_FINNES_HOS_SKD = "/finneshosskatt";

    @Autowired
    protected IdentRepository identRepository;
    @Autowired
    protected TestRestTemplate testRestTemplate;

    protected HttpEntity lagHttpEntity() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
        return new HttpEntity(httpHeaders);
    }

}
