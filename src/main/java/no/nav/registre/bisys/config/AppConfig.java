package no.nav.registre.bisys.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import no.nav.registre.bisys.consumer.rs.BisysSyntetisererenConsumer;
import no.nav.registre.bisys.consumer.rs.HodejegerenConsumer;
import no.nav.registre.bisys.consumer.ui.BisysUiConsumer;

@Configuration
public class AppConfig {

  @Value("${syntrest.rest.api.url}")
  String syntrestServerUrl;

  @Value("${BISYS_TESTSYNT_SAKSBEHANDLER_UID}")
  String saksbehandlerUid;

  @Value("${BISYS_TESTSYNT_SAKSBEHANDLER_PWD}")
  String saksbehandlerPwd;

  @Value("${BISYS_URL}")
  String bisysUrl;

  @Value("${SAKSBEHANDLER_ROLLE}")
  String rolleSaksbehandler;

  @Value("${ENHET}")
  String enhet;

  @Value("${testnorge-hodejegeren.rest-api.url}")
  String hodejegerenServerUrl;

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public BisysSyntetisererenConsumer bisysSyntetisererenConsumer() {
    return new BisysSyntetisererenConsumer(syntrestServerUrl);
  }

  @Bean
  public BisysUiConsumer bisysUiConsumer() {
    return new BisysUiConsumer(
        saksbehandlerUid, saksbehandlerPwd, bisysUrl, rolleSaksbehandler, enhet);
  }

  @Bean
  public HodejegerenConsumer hodejegerenConsumer() {
    return new HodejegerenConsumer(hodejegerenServerUrl);
  }
}
