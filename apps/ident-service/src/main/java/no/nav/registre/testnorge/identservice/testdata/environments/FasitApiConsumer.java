package no.nav.registre.testnorge.identservice.testdata.environments;


import ma.glasnost.orika.MapperFacade;
import no.nav.registre.testnorge.identservice.testdata.config.TpsConstants;
import no.nav.registre.testnorge.identservice.testdata.environments.dao.FasitResource;
import no.nav.registre.testnorge.identservice.testdata.environments.dao.FasitResourceWithUnmappedProperties;
import no.nav.registre.testnorge.identservice.testdata.environments.resourcetypes.FasitMQManager;
import no.nav.registre.testnorge.identservice.testdata.environments.resourcetypes.FasitPropertyTypes;
import no.nav.registre.testnorge.identservice.testdata.factories.QueueManager;
import no.nav.registre.testnorge.identservice.testdata.fasit.Queue;
import no.nav.registre.testnorge.identservice.testdata.url.FasitUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static no.nav.registre.testnorge.identservice.testdata.config.CacheConfig.CACHE_FASIT;
import static no.nav.registre.testnorge.identservice.testdata.environments.resourcetypes.FasitPropertyTypes.QUEUE_MANAGER;
import static no.nav.registre.testnorge.identservice.testdata.url.FasitUrl.createQueryPatternByParamName;

@Service
public class FasitApiConsumer {

    private static final String PREFIX_MQ_QUEUES = "QA.";
    private static final String MID_PREFIX_QUEUE_ENDRING = "_412.";
    private static final String MID_PREFIX_QUEUE_HENTING = "_411.";
    private static final String ZONE = "FSS";
    private static final String FASIT_APP_NAME = "dummy";
    private static final String QUEUE_MANAGER_ALIAS = "mqGateway";

    @Value(value = "${fasit.url}")
    private String fasitUrl;

    @Autowired
    private MapperFacade mapperFacade;

    private final RestTemplate restTemplate;

    public FasitApiConsumer(RestTemplate template) {
        restTemplate = template;
        restTemplate.setMessageConverters(Collections.singletonList(new MappingJackson2HttpMessageConverter()));
    }

    @Cacheable(CACHE_FASIT)
    public FasitResource getScopedResource(String alias, FasitPropertyTypes propertyTypes, String environment) {
        String urlPattern = FasitUrl.SCOPED_RESOURCE_V2_GET.getUrl() +
                createQueryPatternByParamName("alias", "type", "environment", "application", "zone");

        String url = String.format(urlPattern, fasitUrl, alias, propertyTypes.getPropertyName(), environment, FASIT_APP_NAME, ZONE);

        ResponseEntity<FasitResourceWithUnmappedProperties> properties = restTemplate.getForEntity(url, FasitResourceWithUnmappedProperties.class);

        return mapperFacade.map(properties.getBody(), FasitResource.class);
    }


    @Cacheable(CACHE_FASIT)
    public QueueManager getQueueManager(String environment) {

        FasitResource fasitResource = getScopedResource(QUEUE_MANAGER_ALIAS, QUEUE_MANAGER, environment);
        FasitMQManager mqManager = (FasitMQManager) fasitResource.getProperties();
        return QueueManager.builder()
                .name(mqManager.getName())
                .hostname(mqManager.getHostname())
                .port(mqManager.getPort())
                .build();
    }

    public Queue getQueue(String alias, String environment) {

        return Queue.builder()
                .name(new StringBuilder()
                        .append(PREFIX_MQ_QUEUES)
                        .append(environment.toUpperCase())
                        .append(TpsConstants.REQUEST_QUEUE_SERVICE_RUTINE_ALIAS.equals(alias) ?
                                MID_PREFIX_QUEUE_HENTING : MID_PREFIX_QUEUE_ENDRING)
                        .append(alias)
                        .toString())
                .build();
    }
}
