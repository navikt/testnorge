package no.nav.registre.syntrest.services;

import lombok.RequiredArgsConstructor;
import no.nav.registre.syntrest.consumer.SyntConsumer;
import no.nav.registre.syntrest.response.BisysResponse;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BisysService {
    @Value("${synth-arena-bisys-app}")
    private String appName;

    @Value("${synth-arena-bisys-url}")
    private String synthUrl;

    private final BeanFactory beanFactory;
    private final SyntConsumer syntConsumer = beanFactory.getBean(SyntConsumer.class, appName);

    public List<BisysResponse> generateData(int numToGenerate) {
        UriTemplate uri = new UriTemplate(synthUrl);
        RequestEntity request = RequestEntity.get(uri.expand(numToGenerate)).build();
        return (List<BisysResponse>) syntConsumer.synthesizeData(request);
    }
}
