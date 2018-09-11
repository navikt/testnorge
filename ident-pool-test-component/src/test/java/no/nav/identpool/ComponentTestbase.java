package no.nav.identpool;

import java.time.LocalDate;
import java.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Rekvireringsstatus;
import no.nav.identpool.ident.repository.IdentEntity;
import no.nav.identpool.ident.repository.IdentRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ComponentTestConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ComponentTestbase {
    protected static final String IDENT_V1_BASEURL = "/identifikator/v1";
    protected static final String OPERASJON_HENT = "/hent";
    protected static final String OPERASJON_LES = "/les";

    @Autowired
    protected IdentRepository identRepository;
    @Autowired
    protected TestRestTemplate testRestTemplate;
}
