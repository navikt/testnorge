package no.nav.registre.syntrest.services;

import lombok.RequiredArgsConstructor;
import no.nav.registre.syntrest.consumer.SyntConsumer;
import no.nav.registre.syntrest.response.domain.AAP115Melding;
import no.nav.registre.syntrest.response.domain.AAPMelding;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArenaAAPService {
    @Value("synth-arena-aap-app")
    private String appName;

    @Value("synth-arena-aap-115-url")
    private String synth115Url;

    @Value("synth-arena-aap-nyRettighet-url")
    private String synthNyRettighetUrl;

    private final BeanFactory beanFactory;
    private final SyntConsumer syntConsumer = beanFactory.getBean(SyntConsumer.class, appName);

    public List<AAP115Melding> generate115Data(int numToGenerate) {
        UriTemplate uri = new UriTemplate(synth115Url);
        RequestEntity request = RequestEntity.get(uri.expand(numToGenerate)).build();
        return (List<AAP115Melding>) syntConsumer.synthesizeData(request);
    }

    public List<AAPMelding> generateNyRettighetData(int numToGenerate) {
        UriTemplate uri = new UriTemplate(synthNyRettighetUrl);
        RequestEntity request = RequestEntity.get(uri.expand(numToGenerate)).build();
        return (List<AAPMelding>) syntConsumer.synthesizeData(request);
    }
}
