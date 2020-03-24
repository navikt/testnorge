package no.nav.registre.inntektsmeldingstub;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import no.nav.registre.inntektsmeldingstub.config.AppConfig;
import no.nav.registre.inntektsmeldingstub.database.model.Inntektsmelding;
import no.nav.registre.inntektsmeldingstub.database.repository.ArbeidsforholdRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.ArbeidsgiverRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.DelvisFravaerRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.EierRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.EndringIRefusjonRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.GraderingIForeldrePengerRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.InntektsmeldingRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.NaturalytelseDetaljerRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.PeriodeRepository;
import no.nav.registre.inntektsmeldingstub.database.repository.UtsettelseAvForeldrepengerRepository;
import no.nav.registre.inntektsmeldingstub.provider.InntektsmeldingController;
import no.nav.registre.inntektsmeldingstub.service.InntektsmeldingService;
import no.nav.registre.inntektsmeldingstub.service.rs.RsInntektsmelding;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLInntektsmeldingM;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;
import testutil.FileReader;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertTrue;


@RunWith(SpringRunner.class)
@DataJpaTest
@Import({JacksonAutoConfiguration.class, AppConfig.class})
@TestPropertySource(locations = "classpath:application-test.properties")
public class Integrasjonstest {

    private static String jsonFullMeldingRequest;
    private static String jsonFullMeldingAnswer;
    private static String jsonFullMeldingOutXML;

    private static String jsonMinimalMeldingRequest;
    private static String jsonMinimalMeldingAnswer;
    private static String jsonMinimalMeldingOutXML;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ArbeidsforholdRepository arbeidsforholdRepository;
    @Autowired
    private ArbeidsgiverRepository arbeidsgiverRepository;
    @Autowired
    private DelvisFravaerRepository delvisFravaerRepository;
    @Autowired
    private GraderingIForeldrePengerRepository graderingIForeldrePengerRepository;
    @Autowired
    private InntektsmeldingRepository inntektsmeldingRepository;
    @Autowired
    private NaturalytelseDetaljerRepository naturalytelseDetaljerRepository;
    @Autowired
    private PeriodeRepository periodeRepository;
    @Autowired
    private EndringIRefusjonRepository endringIRefusjonRepository;
    @Autowired
    private UtsettelseAvForeldrepengerRepository utsettelseAvForeldrepengerRepository;
    @Autowired
    private EierRepository eierRepository;

    private InntektsmeldingController controller;

    @BeforeClass
    public static void init() throws IOException {
        jsonFullMeldingRequest = FileReader.fileToString("201809/fullMeldingIn.json");
        jsonFullMeldingAnswer = FileReader.fileToString("201809/fullMeldingSavedValues.json");
        jsonFullMeldingOutXML = FileReader.fileToString("201809/fullMeldingOut.xml");

        jsonMinimalMeldingRequest = FileReader.fileToString("201809/minimalMeldingIn.json");
        jsonMinimalMeldingAnswer = FileReader.fileToString("201809/minimalMeldingSavedValues.json");
        jsonMinimalMeldingOutXML = FileReader.fileToString("201809/minimalMeldingOut.xml");
    }

    @Before
    public void setup() {
        flushDatabase();
        InntektsmeldingService service = new InntektsmeldingService(arbeidsgiverRepository, inntektsmeldingRepository, eierRepository);
        controller = new InntektsmeldingController(service);
    }

    private void flushDatabase() {
        arbeidsforholdRepository.deleteAll();
        arbeidsgiverRepository.deleteAll();
        delvisFravaerRepository.deleteAll();
        graderingIForeldrePengerRepository.deleteAll();
        inntektsmeldingRepository.deleteAll();
        naturalytelseDetaljerRepository.deleteAll();
        periodeRepository.deleteAll();
        endringIRefusjonRepository.deleteAll();
        utsettelseAvForeldrepengerRepository.deleteAll();
        eierRepository.deleteAll();
    }

    @Test
    public void testFullMelding() throws IOException {
        RsInntektsmelding melding = jacksonObjectMapper.reader().forType(RsInntektsmelding.class).readValue(jsonFullMeldingRequest);
        List<RsInntektsmelding> meldingListe = Collections.singletonList(melding);

        ResponseEntity<List<Inntektsmelding>> res = controller.opprettInntektsmeldinger201809("test", meldingListe);

        assertThat(res.getStatusCode(), is(HttpStatus.OK));
        assertThat(!Objects.equals(res.getBody(), nullValue()), is(true));
        assertThat(res.getBody().size(), is(1));
        assertThat(jacksonObjectMapper.writeValueAsString(res.getBody()).replaceAll("\\s+", ""), is(jsonFullMeldingAnswer.replaceAll("\\s+", "")));
    }

    @Test
    public void restTemplateExchangeMinimalMelding() {
//        RequestEntity request = RequestEntity.get(new UriTemplate("/2018/09/xml/3").expand()).build();
//        ResponseEntity res = restTemplate.exchange(request, String.class);
        String eier = "test";
        RequestEntity miniRequest = RequestEntity.post(new UriTemplate("https://localhost:8080/2018/09?eier={eier}").expand(eier)).header("Content-Type: application/json").body(jsonMinimalMeldingRequest);
        ResponseEntity response = restTemplate.exchange(miniRequest, Object.class);
        System.out.println(miniRequest.getBody());
    }

}
