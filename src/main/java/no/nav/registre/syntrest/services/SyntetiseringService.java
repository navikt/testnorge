package no.nav.registre.syntrest.services;

import lombok.RequiredArgsConstructor;
import no.nav.registre.syntrest.consumer.SyntConsumer;
import no.nav.registre.syntrest.response.Arbeidsforholdsmelding;
import no.nav.registre.syntrest.response.Barnebidragsmelding;
import no.nav.registre.syntrest.response.Medlemskapsmelding;
import no.nav.registre.syntrest.response.AAP115Melding;
import no.nav.registre.syntrest.response.AAPMelding;
import no.nav.registre.syntrest.response.Institusjonsmelding;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SyntetiseringService {

    private final BeanFactory beanFactory;

    ///////////// NAMING AND URLs //////////////
    @Value("${synth-aareg-app}")
    private String aaregName;
    @Value("${synth-aareg-url}")
    private String aaregUrl;

    @Value("synth-arena-aap-app")
    private String arenaAAPName;
    @Value("synth-arena-aap-115-url")
    private String aap115Url;
    @Value("synth-arena-aap-nyRettighet-url")
    private String aapUrl;

    @Value("${synth-arena-bisys-app}")
    private String bisysName;
    @Value("${synth-arena-bisys-url}")
    private String bisysUrl;

    @Value("${synth-inst-app}")
    private String instName;
    @Value("${synth-inst-url}")
    private String instUrl;

    @Value("${synth-medl-app}")
    private String medlName;
    @Value("${synth-medl-url}")
    private String medlUrl;

    @Value("${synth-arena-meldekort-app}")
    private String meldekortName;
    @Value("${synth-arena-meldekort-url}")
    private String arenaMeldekortUrl;

    ///////////// CONSUMER FACTORY //////////////
    private final SyntConsumer aaregConsumer     = beanFactory.getBean(SyntConsumer.class, aaregName);
    private final SyntConsumer arenaAAPConsumer  = beanFactory.getBean(SyntConsumer.class, arenaAAPName);
    private final SyntConsumer bisysConsumer     = beanFactory.getBean(SyntConsumer.class, bisysName);
    private final SyntConsumer instConsumer      = beanFactory.getBean(SyntConsumer.class, instName);
    private final SyntConsumer medlConsumer      = beanFactory.getBean(SyntConsumer.class, medlName);
    private final SyntConsumer meldekortConsumer = beanFactory.getBean(SyntConsumer.class, meldekortName);


    ///////////// GENERATE FUNCTIONS //////////////
    public List<Arbeidsforholdsmelding> generateAaregData(List<String> fnrs) {
        return (List<Arbeidsforholdsmelding>) generateForFnrs(fnrs, aaregUrl, aaregConsumer);
    }

    public List<AAP115Melding> generateAAP115Data(int numToGenerate) {
        return (List<AAP115Melding>) generateForNumbers(numToGenerate, aap115Url, arenaAAPConsumer);
    }
    public List<AAPMelding> generateAAPData(int numToGenerate) {
        return (List<AAPMelding>) generateForNumbers(numToGenerate, aapUrl, arenaAAPConsumer);
    }

    public List<Barnebidragsmelding> generateBisysData(int numToGenerate) {
        return (List<Barnebidragsmelding>) generateForNumbers(numToGenerate, bisysUrl, bisysConsumer);
    }

    public List<Institusjonsmelding> generateInstData(int numToGenerate) {
        return (List<Institusjonsmelding>) generateForNumbers(numToGenerate, instUrl, instConsumer);
    }

    public List<Medlemskapsmelding> generateMedlData(int numToGenerate) {
        return (List<Medlemskapsmelding>) generateForNumbers(numToGenerate, medlUrl, medlConsumer);
    }

    public List<String> generateMeldekortData(int numToGenerate, String meldegruppe) {
        UriTemplate uri = new UriTemplate(arenaMeldekortUrl);
        RequestEntity request = RequestEntity.get(uri.expand(numToGenerate, meldegruppe)).build();
        return (List<String>) meldekortConsumer.synthesizeData(request);
    }

    ///////////// COMMON FUNCTIONS /////////////
    private Object generateForFnrs(List<String> fnrs, String synthUrl, SyntConsumer syntConsumer) {
        UriTemplate uri = new UriTemplate(synthUrl);
        RequestEntity request = RequestEntity.post(uri.expand()).body(fnrs);
        return syntConsumer.synthesizeData(request);
    }

    private Object generateForNumbers(int numToGenerate, String synthUrl, SyntConsumer syntConsumer) {
        UriTemplate uri = new UriTemplate(synthUrl);
        RequestEntity request = RequestEntity.get(uri.expand(numToGenerate)).build();
        return syntConsumer.synthesizeData(request);
    }
}
