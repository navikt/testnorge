package no.nav.identpool;

import java.time.LocalDate;
import java.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
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

    @Autowired
    private IdentRepository identRepository;
    @Autowired
    protected TestRestTemplate testRestTemplate;

    @Before
    public void polulateDatabase() {
        identRepository.saveAll(Arrays.asList(
                IdentEntity.builder()
                        .identtype(Identtype.FNR)
                        .personidentifikator("10108000398")
                        .rekvireringsstatus(Rekvireringsstatus.LEDIG)
                        .finnesHosSkatt("0")
                        .foedselsdato(LocalDate.of(1980, 10, 10))
                        .build(),
                IdentEntity.builder()
                        .identtype(Identtype.DNR)
                        .personidentifikator("50108000398")
                        .rekvireringsstatus(Rekvireringsstatus.LEDIG)
                        .finnesHosSkatt("0")
                        .foedselsdato(LocalDate.of(1980, 10, 10))
                        .build(),
                IdentEntity.builder()
                        .identtype(Identtype.FNR)
                        .personidentifikator("10108000399")
                        .rekvireringsstatus(Rekvireringsstatus.I_BRUK)
                        .finnesHosSkatt("0")
                        .foedselsdato(LocalDate.of(1980, 10, 10))
                        .build(),
                IdentEntity.builder()
                        .identtype(Identtype.DNR)
                        .personidentifikator("50108000399")
                        .rekvireringsstatus(Rekvireringsstatus.I_BRUK)
                        .finnesHosSkatt("0")
                        .foedselsdato(LocalDate.of(1991, 1, 1))
                        .build()
        ));
    }

    @After
    public void clearDatabase() {
        identRepository.deleteAll();
    }

}
